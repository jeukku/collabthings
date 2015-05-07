package org.collabthings.scripting;

import javax.script.Invocable;
import javax.script.ScriptException;

public interface ScriptLoader {

	Invocable load(String s) throws ScriptException;

}
