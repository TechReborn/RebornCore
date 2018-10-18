/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
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

package reborncore.common.blocks;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.properties.PropertyHelper;

import java.util.*;

/**
 * Created by covers1624 on 2/6/2016.
 */
//Todo: get rid of this abomination of a 1.8 workaround
public class PropertyString extends PropertyHelper<String> {

	private final LinkedList<String> valuesSet;

	public PropertyString(String name, Collection<String> values) {
		super(name, String.class);
		List<String> strList = new ArrayList<>();
		values.forEach(s -> strList.add(s.intern()));
		valuesSet = new LinkedList<>(strList);
	}

	public PropertyString(String name, String... values) {
		super(name, String.class);
		valuesSet = new LinkedList<>();
		Arrays.stream(values).forEach(s -> valuesSet.add(s.intern()));
	}

	@Override
	public Collection<String> getAllowedValues() {
		return ImmutableSet.copyOf(valuesSet);
	}

	@Override
	public Optional<String> parseValue(String value) {
		if (valuesSet.contains(value.intern())) {
			return Optional.of(value.intern());
		}
		return Optional.absent();
	}

	@Override
	public String getName(String value) {
		return value.intern();
	}
}
