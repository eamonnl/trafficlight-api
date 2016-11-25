package com.workday.hackathon.outfox;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.workday.hackathon.outfox.ClewareUsbTrafficLight.Switch;
import com.workday.hackathon.outfox.TrafficLight.Lights;

@SpringBootApplication
@EnableScheduling
public class TrafficLightApplication {

	@Autowired
	private TrafficLight trafficLight;

	// Last light status + Index (for flashing)
	private Lights lastState;
	private int flashState = 0;

	@Autowired
	private ClewareUsbTrafficLight usbDevice;

	@PostConstruct
	public void init() {
		lastState = trafficLight.getState().getLights(); // Should be off on
															// boot
	}

	public static void main(String[] args) {
		SpringApplication.run(TrafficLightApplication.class, args);
	}

	@Scheduled(fixedRate = 1000)
	public void updateTrafficLightDevice() {

		switch (trafficLight.getState().getLights()) {
		case ALLOFF: {
			if (lastState != Lights.ALLOFF) {
				usbDevice.toggleSwitch(Switch.RED, false);
				usbDevice.toggleSwitch(Switch.AMBER, false);
				usbDevice.toggleSwitch(Switch.GREEN, false);
				lastState = Lights.ALLOFF;
			}
			break;
		}
		case ALLON: {
			if (lastState != Lights.ALLON) {
				usbDevice.toggleSwitch(Switch.RED, true);
				usbDevice.toggleSwitch(Switch.AMBER, true);
				usbDevice.toggleSwitch(Switch.GREEN, true);
				lastState = Lights.ALLON;
			}
			break;
		}
		case AMBER: {
			if (lastState != Lights.AMBER) {
				usbDevice.toggleSwitch(Switch.RED, false);
				usbDevice.toggleSwitch(Switch.AMBER, true);
				usbDevice.toggleSwitch(Switch.GREEN, false);
				lastState = Lights.AMBER;
			}
			break;
		}
		case FLASHING_AMBER: {
			if (lastState != Lights.FLASHING_AMBER) {
				flashState = 0;
				lastState = Lights.FLASHING_AMBER;
			}
			usbDevice.toggleSwitch(Switch.RED, false);
			usbDevice.toggleSwitch(Switch.AMBER, (flashState % 2 == 0));
			usbDevice.toggleSwitch(Switch.GREEN, false);
			flashState++;
			break;
		}
		case GREEN: {
			if (lastState != Lights.GREEN) {
				usbDevice.toggleSwitch(Switch.RED, false);
				usbDevice.toggleSwitch(Switch.AMBER, false);
				usbDevice.toggleSwitch(Switch.GREEN, true);
				lastState = Lights.GREEN;
			}
			break;
		}
		case RED: {
			if (lastState != Lights.RED) {
				usbDevice.toggleSwitch(Switch.RED, true);
				usbDevice.toggleSwitch(Switch.AMBER, false);
				usbDevice.toggleSwitch(Switch.GREEN, false);
				lastState = Lights.RED;
			}
			break;
		}
		case FLASH_ALL: {
			if (lastState != Lights.FLASH_ALL) {
				flashState = 0;
				lastState = Lights.FLASH_ALL;
			}
			usbDevice.toggleSwitch(Switch.RED, (flashState % 2 == 0));
			usbDevice.toggleSwitch(Switch.AMBER, (flashState % 2 == 0));
			usbDevice.toggleSwitch(Switch.GREEN, (flashState % 2 == 0));
			flashState++;
			break;
		}

		}

	}

}
