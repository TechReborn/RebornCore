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

import net.minecraftforge.fml.common.event.FMLStateEvent;
import reborncore.api.newRecipe.IRecipeFactory;
import reborncore.common.recipe.RecipeFactoryManager;
import reborncore.common.registration.IRegistryFactory;
import reborncore.common.registration.RegistryConstructionEvent;
import reborncore.common.registration.RegistryTarget;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

@IRegistryFactory.RegistryFactory
public class RecipeFactoryFactory implements IRegistryFactory {

	@Override
	public Class<? extends Annotation> getAnnotation() {
		return RecipeFacotryRegistry.class;
	}

	@Override
	public void handleClass(Class clazz) {
		try {
			IRecipeFactory recipeFactory = (IRecipeFactory) clazz.newInstance();
			RecipeFactoryManager.addFactory(recipeFactory);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Failed to load recipe factory", e);
		}
	}

	@Override
	public List<RegistryTarget> getTargets() {
		return Collections.singletonList(RegistryTarget.CLASS);
	}

	@Override
	public Class<? extends FMLStateEvent> getProcessSate() {
		return RegistryConstructionEvent.class;
	}
}
