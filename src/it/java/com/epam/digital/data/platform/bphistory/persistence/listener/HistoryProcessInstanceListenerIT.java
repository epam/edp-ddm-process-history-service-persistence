/*
 * Copyright 2021 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.bphistory.persistence.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.epam.digital.data.platform.bphistory.model.HistoryProcess;
import com.epam.digital.data.platform.bphistory.persistence.BaseKafkaIT;
import com.epam.digital.data.platform.bphistory.persistence.repository.HistoryProcessRepository;
import java.time.LocalDateTime;
import java.time.Month;
import org.awaitility.Durations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class HistoryProcessInstanceListenerIT extends BaseKafkaIT {

  @Autowired
  private HistoryProcessRepository repository;

  @Test
  void testSavingHistoryProcessInstance() {
    var processId = "processId";
    var startTime = LocalDateTime.of(1986, Month.APRIL, 8, 12, 30);
    var topic = kafkaProperties.getTopics().get("bpm-history-process");
    var request = createHistoryProcess(processId, startTime);

    kafkaTemplate.send(topic, request);

    await().atMost(Durations.TEN_SECONDS).untilAsserted(() -> {
      var savedTask = repository.findById(processId);
      assertThat(savedTask).isNotEmpty();
      var historyTask = savedTask.get();
      assertThat(historyTask.getProcessInstanceId()).isEqualTo(processId);
      assertThat(historyTask.getStartTime()).isEqualTo(startTime);
    });
  }

  private HistoryProcess createHistoryProcess(String id, LocalDateTime startTime) {
    var historyProcess = new HistoryProcess();
    historyProcess.setProcessInstanceId(id);
    historyProcess.setStartTime(startTime);
    return historyProcess;
  }
}
