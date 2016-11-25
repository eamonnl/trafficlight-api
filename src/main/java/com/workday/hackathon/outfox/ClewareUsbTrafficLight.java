package com.workday.hackathon.outfox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.annotation.PreDestroy;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClewareUsbTrafficLight {

	private static final Logger log = LoggerFactory.getLogger(ClewareUsbTrafficLight.class);

	private String deviceSerial;

	public static enum Switch {

		RED(0), AMBER(1), GREEN(2);

		public String number;

		Switch(int no) {
			this.number = Integer.toString(no);
		}

		public String getNumber() {
			return number;
		}

	}

	public ClewareUsbTrafficLight() {
		super();

		// 1. Discover the device ID
		this.deviceSerial = this.getDeviceSerial();

		log.info("Using USB device serial number " + this.deviceSerial);

		// Make Sure they are all turned off to start with
		allSwitchesOff();

	}

	@PreDestroy
	private void allSwitchesOff() {

		// Turn off the lights
		Switch[] switches = Switch.values();
		for (int i = 0; i < switches.length; i++) {
			this.toggleSwitch(switches[i], false);
		}

	}

	public void toggleSwitch(Switch sw, boolean on) {

		try {

			execute(new String[] { "clewarecontrol", "-d", this.deviceSerial, "-c", "1", "-as", sw.getNumber(),
					((on) ? "1" : "0") });

		} catch (Exception e) {
			log.error("Failed to toggle device switch", e);
		}

	}

	private String getDeviceSerial() {

		String serial = null;
		try {
			serial = execute(new String[] { "clewarecontrol", "-l" });

			log.info(serial);

			// Take the last number in string

			String[] splitOutput = serial.split("\\s+");

			return splitOutput[splitOutput.length - 1];

		} catch (IOException | InterruptedException e) {
			log.error("Failed to read device serial number", e);
		}

		return null;
	}

	private synchronized String execute(String[] command) throws IOException, InterruptedException {

		log.info("Executing command " + StringUtils.join(command, " "));

		Process process = new ProcessBuilder(command).start();

		return this.executeProcess(process, false);

	}

	/**
	 * Reads the processes streams.
	 * 
	 * @param process
	 *            the executing process to read streams from
	 * @param failOnError
	 *            if true an exception will be thrown if the process exits with
	 *            an error code
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private String executeProcess(Process process, boolean failOnError) throws IOException, InterruptedException {
		StringBuffer stdOutBuffer = new StringBuffer();
		StringBuffer stdErrBuffer = new StringBuffer();

		try {
			// Read what the process outputs
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				stdOutBuffer.append(line);
				stdOutBuffer.append('\n');
			}
			input.close();

			if (failOnError) {
				BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				String errorLine = null;
				while ((errorLine = error.readLine()) != null) {
					stdErrBuffer.append(errorLine);
					stdErrBuffer.append(" ");
				}
				error.close();
			}

			process.waitFor();

			if (failOnError && process.exitValue() != 0)
				throw new IllegalStateException("Forked process '" + process.toString() + "' exited with error code "
						+ process.exitValue() + ". " + stdErrBuffer.toString());

		} finally {
			// Close process streams
			if (process != null) {
				IOUtils.closeQuietly(process.getOutputStream());
				IOUtils.closeQuietly(process.getInputStream());
				IOUtils.closeQuietly(process.getErrorStream());
			}
		}

		return stdOutBuffer.toString();
	}

}
