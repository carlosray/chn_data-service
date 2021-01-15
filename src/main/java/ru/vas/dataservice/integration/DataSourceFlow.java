package ru.vas.dataservice.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.integration.dsl.*;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.handler.advice.AbstractRequestHandlerAdvice;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.TopicPartitionOffset;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import ru.vas.dataservice.db.domain.BlockedResource;
import ru.vas.dataservice.db.domain.UpdateResource;
import ru.vas.dataservice.model.SaveInfo;
import ru.vas.dataservice.service.BlockedResourceService;
import ru.vas.dataservice.service.UpdateResourceService;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@EnableIntegration
@Configuration
@Slf4j
@RequiredArgsConstructor
public class DataSourceFlow {
    private final BlockedResourceService blockedResourceService;
    private final UpdateResourceService updateResourceService;

    @Value("${data-service.kafka.consumer.aggregate.messages-count}")
    private Integer aggregateMessagesCount = 1000;
    @Value("${data-service.kafka.consumer.aggregate.time-out-delay}")
    private Duration aggregateTimeOut = Duration.of(10, ChronoUnit.SECONDS);

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
                .<UpdateResource>filter(updateResource -> !updateResourceService.exists(updateResource))
                .<UpdateResource>handle((updateResource, headers) -> updateResourceService.saveNew(updateResource))
                .<UpdateResource>log(message -> String.format(
                        "Сохранен UpdateResource с id '%s'",
                        message.getPayload().getCorrelationId()));
    }

    @Bean
    public IntegrationFlow toMongoDbFlow() {
        return toMongoDbFlow -> toMongoDbFlow
                .channel(MessageChannels.executor(this.executor()))
                .<BlockedResource>handle(this::setUpdateToBlockedResource)
                .aggregate(aggregatorSpecConfig())
                .handle(updateBlockedResource(),
                        e -> e.advice(logTime("Сохранение заблокированных ресурсов")));
    }

    private BlockedResource setUpdateToBlockedResource(BlockedResource blockedResource, MessageHeaders headers) {
        blockedResource.setUpdate(updateResourceService.getUpdateInfo(headers));
        return blockedResource;
    }

    private GenericHandler<List<BlockedResource>> updateBlockedResource() {
        return (blockedResource, headers) -> blockedResourceService.findSameAndSetUpdate(blockedResource);
    }

    private Consumer<AggregatorSpec> aggregatorSpecConfig() {
        return aggregatorSpec -> aggregatorSpec
                .releaseStrategy(messageGroup -> messageGroup.size() == aggregateMessagesCount)
                .expireGroupsUponCompletion(true)
                .groupTimeout(aggregateTimeOut.toMillis())
                .sendPartialResultOnExpiry(true);
    }

    @Bean
    public IntegrationFlow errorFlow() {
        return IntegrationFlows.from(IntegrationContextUtils.ERROR_CHANNEL_BEAN_NAME)
                .handle(m -> log.error("Ошибка в Integration Flow {}", m))
                .get();
    }

    public Advice logTime(String msg) {
        return new AbstractRequestHandlerAdvice() {
            @Override
            protected Object doInvoke(ExecutionCallback callback, Object target, Message<?> message) {
                final Instant before = Instant.now();
                final Object execute = callback.execute();
                final Instant after = Instant.now();
                log.info(msg + " = " + Duration.between(before, after).toMillis() + " ms." + getInfoStatistics(execute));
                return execute;
            }

            private String getInfoStatistics(Object execute) {
                if (execute instanceof SaveInfo) {
                    return " Сохранено: " + ((SaveInfo) execute).getCreated() + ". Обновлено: " + ((SaveInfo) execute).getUpdated();
                }
                else {
                    return "";
                }
            }
        };
    }
}
