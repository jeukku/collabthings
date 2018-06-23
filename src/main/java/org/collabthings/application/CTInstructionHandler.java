package org.collabthings.application;

import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.model.impl.CTApplicationImpl.ApplicationLine;

public interface CTInstructionHandler {

	void handle(ApplicationLine line, CTRunEnvironment rune);

}
