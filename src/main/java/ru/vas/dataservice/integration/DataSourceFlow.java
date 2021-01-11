package ru.vas.dataservice.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.TopicPartitionOffset;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import ru.vas.dataservice.db.domain.BlockedResource;
import ru.vas.dataservice.db.domain.UpdateResource;
import ru.vas.dataservice.service.BlockedResourceService;
import ru.vas.dataservice.service.UpdateResourceService;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@EnableIntegration
@Configuration
@Slf4j
@RequiredArgsConstructor
public class DataSourceFlow {
    private final BlockedResourceService blockedResourceService;
    private final UpdateResourceService updateResourceService;

    @Bean
    public Executor executor() {
        return Executors.newFixedThreadPool(10);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public IntegrationFlow kafkaReadingFlow(ConsumerFactory<String, BlockedResource> consumerFactory,
                                            @Qualifier("topicPartitions") TopicPartitionOffset[] topicPartitionOffsets,
                                            @Value("${data-service.kafka.consumer.polling-delay}") Duration duration,
                                            @Value("${data-service.kafka.consumer.messages-per-poll}") Integer messagesPerPoll) {
        return IntegrationFlows.from(
                Kafka.inboundChannelAdapter(
                        consumerFactory, new ContainerProperties(topicPartitionOffsets))
                        .payloadType(BlockedResource.class),
                e -> e.poller(Pollers
                        .fixedDelay(duration)
                        .maxMessagesPerPoll(messagesPerPoll)))
                .routeToRecipients(recipientSpec -> recipientSpec
                        .recipientFlow(updateFlow())
                        .recipientFlow(toMongoDbFlow()))
                .get();
    }

    public IntegrationFlow updateFlow() {
        return updateFlow -> updateFlow
                .transform(Message.class, message -> updateResourceService.getUpdateInfo(message.getHeaders()))
                .<UpdateResource>filter(updateResourceService::notExists)
                .<UpdateResource>handle((updateResource, headers) -> updateResourceService.saveNew(updateResource))
                .<UpdateResource>log(message -> String.format(
                        "Сохранен UpdateResource с id '%s'",
                        message.getPayload().getCorrelationId()));
    }

    @Bean
    public IntegrationFlow toMongoDbFlow() {
        return toMongoDbFlow -> toMongoDbFlow
                .filter(blockedResourceService::sameNotExists, e -> e
                        .discardFlow(flow -> flow
                                .handle(updateBlockedResource())))
                .handle(this::setUpdateToBlockedResource)
                .<BlockedResource>handle((blockedResource, headers) -> blockedResourceService.saveNew(blockedResource))
                .log(message -> "AFTER SAVING " + message.toString());
    }

    private BlockedResource setUpdateToBlockedResource(BlockedResource blockedResource, MessageHeaders headers) {
        blockedResource.setUpdate(updateResourceService.getUpdateInfo(headers));
        return blockedResource;
    }

    private GenericHandler<BlockedResource> updateBlockedResource() {
        return (blockedResource, headers) -> blockedResourceService.findSameAndSetUpdate(blockedResource, updateResourceService.getUpdateInfo(headers));
    }

    @Bean
    public IntegrationFlow errorFlow() {
        return IntegrationFlows.from(IntegrationContextUtils.ERROR_CHANNEL_BEAN_NAME)
                .handle(m -> log.error("Ошибка в Integration Flow {}", m))
                .get();
    }
}
