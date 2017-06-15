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

/*******************************************************************************
 * Copyright (c) 2015 Jeff Martin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * Jeff Martin - initial API and implementation
 ******************************************************************************/

package reborncore.mixin.transformer.util;

import com.google.common.base.Charsets;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ConstInfoAccessor {

	private static Class<?> clazz;
	private static Field index;
	private static Method getTag;

	private Object item;

	public ConstInfoAccessor(Object item) {
		if (item == null) {
			throw new IllegalArgumentException("item cannot be null!");
		}
		this.item = item;
	}

	public Object getItem() {
		return this.item;
	}

	public int getIndex() {
		try {
			return (Integer) index.get(this.item);
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}

	public int getTag() {
		try {
			return (Integer) getTag.invoke(this.item);
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}

	public ConstInfoAccessor copy() {
		return new ConstInfoAccessor(copyItem());
	}

	public Object copyItem() {
		// I don't know of a simpler way to copy one of these silly things...
		try {
			// serialize the item
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(buf);
			write(out);

			// deserialize the item
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(buf.toByteArray()));
			Object item = new ConstInfoAccessor(in).getItem();
			in.close();

			return item;
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}

	public void write(DataOutputStream out) throws IOException {
		try {
			out.writeUTF(this.item.getClass().getName());
			out.writeInt(getIndex());

			Method method = this.item.getClass().getMethod("write", DataOutputStream.class);
			method.setAccessible(true);
			method.invoke(this.item, out);
		} catch (IOException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}

	@Override
	public String toString() {
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			PrintWriter out = new PrintWriter(new OutputStreamWriter(buf, Charsets.UTF_8));
			Method print = this.item.getClass().getMethod("print", PrintWriter.class);
			print.setAccessible(true);
			print.invoke(this.item, out);
			out.close();
			return buf.toString("UTF-8").replace("\n", "");
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}

	static {
		try {
			clazz = Class.forName("javassist.bytecode.ConstInfo");
			index = clazz.getDeclaredField("index");
			index.setAccessible(true);
			getTag = clazz.getMethod("getTag");
			getTag.setAccessible(true);
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}
}
