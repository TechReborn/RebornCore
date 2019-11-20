/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reborncore.api.scriba;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.registry.GameRegistry;

import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author estebes
 */
public class TileRegistrationManager {
    private TileRegistrationManager(String domain) {
        if (StringUtils.isBlank(domain)) throw new IllegalArgumentException("The domain cannot be blank");

        this.domain = domain;
        this.logger = LogManager.getLogger(domain + "|Scriba");
    }

    public void registerTiles() {
        try (ScanResult result = new ClassGraph().enableClassInfo().enableAnnotationInfo().scan()) {
            ClassInfoList classInfoList = result.getClassesWithAnnotation(RegisterTile.class.getName());

            classInfoList.filter(reference -> reference.extendsSuperclass(TileEntity.class.getName()))
                    .forEach(tileReference -> {
                        AnnotationParameterValueList annotationParameters =
                                tileReference.getAnnotationInfo(RegisterTile.class.getName()).getParameterValues();

                        String name = (String) annotationParameters.getValue("name");

                        ResourceLocation resourceLocation = new ResourceLocation(domain, name);

                        GameRegistry.registerTileEntity(tileReference.loadClass().asSubclass(TileEntity.class), resourceLocation);

                        if (registeredTiles.put(resourceLocation, tileReference.loadClass().asSubclass(TileEntity.class)) != null)
                            logger.error(String.format("The tile with ResourceLocation %s was already registered before.", resourceLocation.toString()));
                    });

            classInfoList.filter(reference -> !reference.extendsSuperclass(TileEntity.class.getName()))
                    .forEach(badReference -> logger.error(
                            String.format("The class %s annotated with %s was ignored because it is not a subclass of %s. ",
                                    badReference.getName(), RegisterTile.class.getName(), TileEntity.class.getName())));
        }
    }

    // Fields >>
    private final String domain;
    private final Logger logger;
    private final ConcurrentHashMap<ResourceLocation, Class<? extends TileEntity>> registeredTiles = new ConcurrentHashMap<>();
    // << Fields
}
