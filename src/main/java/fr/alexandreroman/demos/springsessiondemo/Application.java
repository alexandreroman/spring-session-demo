/*
 * Copyright (c) 2019 Pivotal Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.alexandreroman.demos.springsessiondemo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Extract information about current application instance.
     */
    @Bean
    public InstanceInfo instanceName(ObjectMapper objectMapper, @Value("spring.application.name") String springAppName) throws IOException {
        String instanceIndexStr = System.getenv("CF_INSTANCE_INDEX");
        if (instanceIndexStr == null) {
            instanceIndexStr = "0";
        }
        final int instanceIndex = Integer.parseInt(instanceIndexStr);

        final String appName;
        final String vcapApplicationJson = System.getenv("VCAP_APPLICATION");
        if (vcapApplicationJson == null) {
            appName = springAppName;
        } else {
            final VcapApplication vcapApplication = objectMapper.readValue(vcapApplicationJson, VcapApplication.class);
            appName = vcapApplication.applicationName;
        }
        return new InstanceInfo(appName, instanceIndex);
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class VcapApplication {
        @JsonProperty("application_name")
        private String applicationName;
    }
}

@RequiredArgsConstructor
@Getter
class InstanceInfo {
    private final String name;
    private final int index;

    @Override
    public String toString() {
        return name + "/" + index;
    }
}

@Controller
@RequiredArgsConstructor
@Slf4j
class IndexController {
    private static final String SESSION_ATTR_COUNTER = "counter";
    private final InstanceInfo instanceInfo;

    @GetMapping("/")
    public String index(Model model, HttpServletRequest req) {
        // Get user session.
        final HttpSession session = req.getSession();
        AtomicInteger counter = (AtomicInteger) session.getAttribute(SESSION_ATTR_COUNTER);
        if (counter == null) {
            counter = new AtomicInteger();
        }

        // Update some value in user session.
        // This session is replicated across application instances.
        counter.incrementAndGet();
        session.setAttribute(SESSION_ATTR_COUNTER, counter);

        model.addAttribute("instanceName", instanceInfo.toString());

        return "index";
    }
}
