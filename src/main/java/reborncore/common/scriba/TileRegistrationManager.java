package reborncore.common.scriba;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.registry.GameRegistry;

import reborncore.api.scriba.TileReference;

import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

public class TileRegistrationManager {
    public static final TileRegistrationManager INSTANCE = new TileRegistrationManager();

    private TileRegistrationManager() {
        // NO-OP
    }

    public void registerReferencedTiles() {
        try (ScanResult result = new ClassGraph().enableClassInfo().enableAnnotationInfo().scan()) {
            ClassInfoList classInfoList = result.getClassesWithAnnotation(TileReference.class.getName());

            classInfoList.stream()
                    .filter(tileReference -> tileReference.extendsSuperclass(TileEntity.class.getName()))
                    .forEach(tileReference -> {
                        AnnotationParameterValueList annotationParameters =
                                tileReference.getAnnotationInfo(TileReference.class.getName()).getParameterValues();

                        String resourceDomain = (String) annotationParameters.getValue("resourceDomain");
                        String resourcePath = (String) annotationParameters.getValue("resourcePath");

                        ResourceLocation resourceLocation = new ResourceLocation(resourceDomain, resourcePath);

                        GameRegistry.registerTileEntity(tileReference.getClass().asSubclass(TileEntity.class), resourceLocation);

                        if (registeredTiles.put(resourceLocation, tileReference.getClass().asSubclass(TileEntity.class)) != null)
                            LOGGER.error(String.format("The tile with ResourceLocation %s was already registered before.", resourceLocation.toString()));
                    });
        }
    }

    // Fields >>
    public static final Logger LOGGER = LogManager.getLogger("team_reborn|Scriba");

    private final ConcurrentHashMap<ResourceLocation, Class<? extends TileEntity>> registeredTiles = new ConcurrentHashMap<>();
    // << Fields
}
