package org.nyu.crypto.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.nyu.crypto.dto.ClimbPaper;
import org.nyu.crypto.dto.ClimbSample;
import org.nyu.crypto.service.strategy.HillClimberPaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;

import org.nyu.crypto.service.HttpClient;

@RestController
public class ClimberController {
	@Autowired
	HttpClient httpClient;
	
	@Autowired
	HillClimberPaper hillClimberPaper;
	
	@RequestMapping(value = "/api/climb/plaintext", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> performPerfectPlainText(@RequestBody String body) throws UnsupportedEncodingException {
		String deurl = URLDecoder.decode(body,"UTF-8");
		Gson gson = new Gson();
		deurl = deurl.substring(0,deurl.length()-1);
		ClimbSample sample =gson.fromJson(deurl, ClimbSample.class);
        ClimbPaper climb = hillClimberPaper.climbPlaintextDigraph(sample.getCiphertext(), sample.getDigraph());
        String result = climb.getPutative();
//        System.out.println(result);
        return ResponseEntity.ok(result);
    }


}
