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

package com.epam.digital.data.platform.bphistory.persistence;

import com.epam.digital.data.platform.starter.kafka.config.properties.KafkaProperties;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

@SpringBootTest
@Import(BaseKafkaIT.TestKafkaConfig.class)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1,
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@AutoConfigureEmbeddedDatabase(provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY)
public abstract class BaseKafkaIT {

  @Autowired
  protected KafkaProperties kafkaProperties;
  @Autowired
  protected KafkaTemplate<String, Object> kafkaTemplate;
  @Autowired
  private EmbeddedKafkaBroker embeddedKafkaBroker;
  @Autowired
  private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

  @BeforeEach
  public void setUp() {
    for (var messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers()) {
      ContainerTestUtils.waitForAssignment(messageListenerContainer,
          embeddedKafkaBroker.getPartitionsPerTopic());
    }
  }

  @TestConfiguration
  static class TestKafkaConfig {

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(KafkaProperties kafkaProperties) {
      Map<String, Object> producerProps =
          Map.of(
              ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
              kafkaProperties.getBootstrap(),
              ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
              StringSerializer.class,
              ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
              JsonSerializer.class);
      return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps));
    }
  }
}
