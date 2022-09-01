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

import com.epam.digital.data.platform.bphistory.model.HistoryTask;
import com.epam.digital.data.platform.bphistory.persistence.BaseKafkaIT;
import com.epam.digital.data.platform.bphistory.persistence.repository.HistoryTaskRepository;
import java.time.LocalDateTime;
import java.time.Month;
import org.awaitility.Durations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class HistoryTaskListenerIT extends BaseKafkaIT {

  @Autowired
  private HistoryTaskRepository historyTaskRepository;

  @Test
  void testSaveHistoryTask() {
    var taskId = "taskId";
    var startTime = LocalDateTime.of(1986, Month.APRIL, 8, 12, 30);
    var request = createRequest(taskId, startTime);
    var topic = kafkaProperties.getTopics().get("bpm-history-task");

    kafkaTemplate.send(topic, request);

    await().atMost(Durations.TEN_SECONDS).untilAsserted(() -> {
      var savedTask = historyTaskRepository.findById(taskId);
      assertThat(savedTask).isNotEmpty();
      var historyTask = savedTask.get();
      assertThat(historyTask.getActivityInstanceId()).isEqualTo(taskId);
      assertThat(historyTask.getStartTime()).isEqualTo(startTime);
    });
  }

  private HistoryTask createRequest(String taskId, LocalDateTime startTime) {
    var historyTask = new HistoryTask();
    historyTask.setActivityInstanceId(taskId);
    historyTask.setStartTime(startTime);
    return historyTask;
  }
}