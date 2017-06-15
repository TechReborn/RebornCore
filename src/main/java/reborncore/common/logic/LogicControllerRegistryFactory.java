/*
 * Copyright (c) 2017 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reborncore.common.logic;

import reborncore.common.registration.IRegistryFactory;
import reborncore.common.registration.RegistrationManager;
import reborncore.common.registration.RegistryTarget;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

@IRegistryFactory.RegistryFactory
public class LogicControllerRegistryFactory implements IRegistryFactory {

	@Override
	public Class<? extends Annotation> getAnnotation() {
		return LogicControllerRegistry.class;
	}

	@Override
	public void handleField(Field field) {
		Class clazz = field.getType();
		if (!Modifier.isStatic(field.getModifiers())) {
			throw new RuntimeException("Field must be static when used with RebornBlockRegistry");
		}
		try {
			LogicControllerRegistry annotation = (LogicControllerRegistry) RegistrationManager.getAnnoationFromArray(field.getAnnotations(), this);
			if (annotation == null || annotation.name().isEmpty()) {
				throw new RuntimeException("No name provided");
			}

			LogicController tile = (LogicController) clazz.newInstance();
			tile.setName(annotation.name());
			LogicBlock block = new LogicBlock(tile);
			LogicUtils.registerLogicController(block, tile);
			field.set(null, tile);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<RegistryTarget> getTargets() {
		return Collections.singletonList(RegistryTarget.FIELD);
	}
}
