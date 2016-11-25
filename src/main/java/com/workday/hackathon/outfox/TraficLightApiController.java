package com.workday.hackathon.outfox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.workday.hackathon.outfox.TrafficLight.TrafficLightState;

@RestController
public class TraficLightApiController {

	private static final Logger log = LoggerFactory.getLogger(TraficLightApiController.class);

	private final TrafficLight trafficLight;

	@Autowired
	public TraficLightApiController(TrafficLight trafficLight) {
		this.trafficLight = trafficLight;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/")
	public TrafficLightState getState() {
		synchronized (trafficLight) {
			TrafficLightState state = trafficLight.getState();
			log.info("Traffic light state {" + state.getLights().name() + "}");
			return state;
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/")
	public void changeState(@RequestBody TrafficLightState state) {
		synchronized (trafficLight) {
			log.info("Setting traffic light state {" + state.getLights().name() + "}");
			trafficLight.setState(state);
		}
	}

}
