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

package reborncore.common.util;

import net.minecraftforge.fml.common.ICrashCallable;
import reborncore.RebornCore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by modmuss50 on 01/02/2017.
 */
public class CrashHandler implements ICrashCallable {
	@Override
	public String getLabel() {
		return "RebornCore";
	}

	@Override
	public String call() throws Exception {
		StringBuilder builder = new StringBuilder();
		for (String str : getInfo()) {
			builder.append("\n" + "\t\t" + str);
		}
		return builder.toString();
	}

	public List<String> getInfo() {
		List<String> str = new ArrayList<>();
		RebornCore.proxy.getCrashData(str);
		return str;
	}
}
