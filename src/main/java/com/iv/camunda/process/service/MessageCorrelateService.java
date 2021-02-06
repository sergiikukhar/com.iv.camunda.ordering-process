package com.iv.camunda.process.service;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.springframework.stereotype.Service;


@Service
public class MessageCorrelateService {

    public void correlate(DelegateExecution execution, String messageEventName) {
        String processInstanceId = execution.getProcessInstanceId();
        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
        EventSubscription eventSubscription = lookUpForEventSubscription(processInstanceId, runtimeService, messageEventName);
        if (eventSubscription != null) {
            sendNotificationForProcessInstanceMessageEvent(runtimeService, processInstanceId, messageEventName);
            return;
        }
    }

    private void sendNotificationForProcessInstanceMessageEvent(RuntimeService runtimeService, String processInstanceId, String messageEventName) {
        runtimeService.createMessageCorrelation(messageEventName)
                .processInstanceId(processInstanceId)
                .correlate();
    }

    private EventSubscription lookUpForEventSubscription(String processInstanceId, RuntimeService runtimeService, String messageEventName) throws ProcessEngineException {
        return runtimeService.createEventSubscriptionQuery()
                .processInstanceId(processInstanceId)
                .eventName(messageEventName)
                .singleResult();
    }

}
