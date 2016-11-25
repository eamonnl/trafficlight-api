package com.workday.hackathon.outfox;

import org.springframework.stereotype.Component;

@Component
public class TrafficLight {

	private Lights state;

	public TrafficLight() {
		this.state = Lights.ALLOFF;
	}

	public TrafficLightState getState() {
		return new TrafficLightState(this.state);
	}

	public void setState(TrafficLightState state) {
		this.state = state.getLights();
	}

	
	/**
	 * Wraps the enum to create valid json object. This object should be immutable
	 */
	public static class TrafficLightState {
		
		private Lights lights;

		public TrafficLightState() {
			super();
		}

		public TrafficLightState(Lights lights) {
			this();
			this.lights = lights;
		}

		public Lights getLights() {
			return lights;
		}
		
	}
	
	public static enum Lights {
		GREEN, AMBER, FLASHING_AMBER, RED, ALLOFF, ALLON, FLASH_ALL;
	}
	
}
