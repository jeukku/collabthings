package org.collabthings.application.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.collabthings.application.CTInstructionHandler;
import org.collabthings.application.handlers.lines.CTVectorApplicationLine;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.model.CTObject;
import org.collabthings.model.CTValues;
import org.collabthings.model.impl.CTApplicationImpl.ApplicationLine;

// this should ne removed. Calling methods with a name is too dangerous.
public class CTPartHandler implements CTInstructionHandler {

	private static final String PARAM_ACTION = "action";
	private static final String ACTION_CALL = "call";

	public CTPartApplicationLine part() {
		return new CTPartApplicationLine();
	}

	@Override
	public void handle(ApplicationLine instruction, CTRunEnvironment rune, CTValues values) {
		String action = instruction.get("action");
		if (ACTION_CALL.equals(action)) {
			String dest = instruction.get("dest");
			String source = instruction.get("source");
			String method = instruction.get("method");

			CTObject o = rune.getObject(source);
			try {
				Method m = o.getClass().getMethod(method, null);
				Object ret = m.invoke(o, null);
				if (ret == null) {
					rune.resetValue(dest);
				} else if (ret instanceof CTObject) {
					rune.addObject(dest, (CTObject) ret);
				} else {
					rune.setParameter(dest, ret.toString());
				}
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new RuntimeException("action type " + action + " not supported");
		}
	}

	public static class SetCallApplicationLine extends ApplicationLine {

		public SetCallApplicationLine(String dest, String source, String method) {
			put("a", "set");
			put(PARAM_ACTION, ACTION_CALL);
			put("dest", dest);
			put("source", source);
			put("method", method);
		}
	}

	public static class CTPartApplicationLine extends ApplicationLine {
		public CTPartGetSubApplicationLine getSub(String name) {
			return new CTPartGetSubApplicationLine(name);
		}
	}

	public static class CTPartGetSubApplicationLine extends ApplicationLine {

		public CTPartGetSubApplicationLine(String name) {
			// TODO Auto-generated constructor stub
		}

		public CTVectorApplicationLine location() {
			return new CTVectorApplicationLine();
		}
	}
}
