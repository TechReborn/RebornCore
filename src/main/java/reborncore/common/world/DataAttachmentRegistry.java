/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020 TechReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package reborncore.common.world;

import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.function.Supplier;

public class DataAttachmentRegistry {

	private static HashMap<Class<? extends DataAttachment>, Supplier<? extends DataAttachment>> dataAttachments = new HashMap<>();

	private static boolean locked = false;

	public <T extends DataAttachment> void register(Class<T> clazz, Supplier<T> supplier){
		Validate.isTrue(!locked, "Data attachment registry is locked!");
		if(dataAttachments.containsKey(clazz)){
			throw new UnsupportedOperationException("Data attachment already registered for " + clazz.getName());
		}
		dataAttachments.put(clazz, supplier);
	}

	public HashMap<Class<? extends DataAttachment>, Supplier<? extends DataAttachment>> getAllDataAttachments(){
		Validate.isTrue(locked, "Data attachment registry is not locked!");
		return dataAttachments;
	}

	public void lock(){
		locked = true;
	}

}
