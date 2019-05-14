package org.nyu.crypto.service;

import org.nyu.crypto.dto.Ciphertext;
import org.nyu.crypto.dto.Digraph;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import io.swagger.models.HttpMethod;
import springfox.documentation.spring.web.json.Json;

@Service
public class HttpClient {
	public Ciphertext Cipherclient(String url,HttpMethod method,MultiValueMap<Json, Json> params) {
		RestTemplate template = new RestTemplate();
		ResponseEntity<Ciphertext> response = template.getForEntity(url, Ciphertext.class);
//		System.out.println(response.getBody().getClass());
		return response.getBody();
	}
	public Digraph Digraphclient(String url,HttpMethod method,MultiValueMap<Json, Json> params) {
		RestTemplate template = new RestTemplate();
		ResponseEntity<Digraph> response = template.getForEntity(url, Digraph.class);
//		System.out.println(response.getBody().getClass());
		return response.getBody();
	}
}