/*
 * Copyright 2016-2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.snowdrop.example.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.micrometer.core.instrument.Metrics;

@RestController
@RequestMapping(value = "/api/fruits")
public class FruitController {
    final static String SERVICE_HOST = "SERVICE_HOST";
    final static String SERVICE_PORT = "SERVICE_PORT";

    final String serviceHost = System.getenv(SERVICE_HOST) == null ||  System.getenv(SERVICE_HOST).length() <= 0 ? "fruit-service" : System.getenv(SERVICE_HOST);
    final String servicePort = System.getenv(SERVICE_PORT) == null ||  System.getenv(SERVICE_PORT).length() <= 0 ? "8080" : System.getenv(SERVICE_PORT);
    
    final String serviceURL = "http://" + serviceHost + ":" + servicePort + "/api/fruits";

    @Autowired
    RestTemplate restTemplate;

    public FruitController() {
    }

    @GetMapping("/{id}")
    public Fruit get(@PathVariable("id") Integer id) {
        // >>> Prometheus metric
        Metrics.counter("api.http.requests.total", "api", "inventory", "method", "GET", "endpoint", 
            "/inventory/" + id).increment();
        // <<< Prometheus metric
        
        return restTemplate.getForObject(serviceURL + "/1", Fruit.class);
    }

    @GetMapping
    public List<Fruit> getAll() {
        // Prometheus metric
        Metrics.counter("api.http.requests.total", "api", "inventory", "method", "GET", "endpoint", 
        "/inventory").increment();
        // <<< Prometheus metric
        
        Fruit[] fruits = restTemplate.getForObject(serviceURL, Fruit[].class);

        return Arrays.asList(fruits);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Fruit post(@RequestBody(required = false) Fruit fruit) {
        return restTemplate.postForObject(serviceURL, fruit, Fruit.class);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public Fruit put(@PathVariable("id") Integer id, @RequestBody(required = false) Fruit fruit) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id.toString());

        restTemplate.put(serviceURL + "/{id}", fruit, params);

        return fruit;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {    
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id.toString());
        
        restTemplate.delete(serviceURL + "/{id}",  params );
    }
}
