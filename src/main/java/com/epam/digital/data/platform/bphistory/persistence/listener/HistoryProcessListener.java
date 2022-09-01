/*
 * Copyright 2021 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.bphistory.persistence.listener;

import com.epam.digital.data.platform.bphistory.model.HistoryProcess;
import com.epam.digital.data.platform.bphistory.persistence.audit.AuditableListener;
import com.epam.digital.data.platform.bphistory.persistence.audit.AuditableListener.Operation;
import com.epam.digital.data.platform.bphistory.persistence.service.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class HistoryProcessListener {

  private final Logger log = LoggerFactory.getLogger(HistoryProcessListener.class);

  private final ProcessService processService;

  public HistoryProcessListener(ProcessService processService) {
    this.processService = processService;
  }

  @AuditableListener(Operation.UPDATE)
  @KafkaListener(
      topics = "\u0023{kafkaProperties.topics['bpm-history-process']}",
      groupId = "\u0023{kafkaProperties.consumer.groupId}",
      containerFactory = "concurrentKafkaListenerContainerFactory")
  public void save(HistoryProcess input) {
    log.info("Kafka event received");
    if (input != null) {
      log.info(
          "Save Process with id: {}",
          input.getProcessInstanceId());
      processService.save(input);
    }
  }
}
