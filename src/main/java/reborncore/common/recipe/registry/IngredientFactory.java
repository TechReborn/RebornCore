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

package reborncore.common.recipe.registry;

import com.google.gson.JsonObject;
import reborncore.common.recipe.IngredientParser;
import reborncore.common.registration.IRegistryFactory;
import reborncore.common.registration.RegistryTarget;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

@IRegistryFactory.RegistryFactory
public class IngredientFactory implements IRegistryFactory {

	@Override
	public Class<? extends Annotation> getAnnotation() {
		return IngredientRegistry.class;
	}

	@Override
	public void handleClass(Class clazz) {
		validateClass(clazz);
		try {
			IngredientParser.addIngredient(clazz);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
			throw new RuntimeException("Failed to load ingredient", e);
		}
	}

	private void validateClass(Class clazz){
		boolean hasEmptyConstructor = false;
		for (Constructor constructor : clazz.getConstructors()) {
			if (constructor.getParameterCount() == 0) {
				hasEmptyConstructor = true;
			}
		}
		if (!hasEmptyConstructor) {
			throw new RuntimeException("The ingredient " + clazz.getName() + " does not have an empty constructor!");
		}

		try {
			Method method = clazz.getDeclaredMethod("fromJson", JsonObject.class);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("The ingredient " + clazz.getName() + " does not have a valid fromJson method");
		}
	}

	@Override
	public List<RegistryTarget> getTargets() {
		return Collections.singletonList(RegistryTarget.CLASS);
	}
}
