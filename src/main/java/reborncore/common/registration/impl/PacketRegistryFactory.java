package reborncore.common.registration.impl;

import org.apache.commons.lang3.tuple.Pair;
import reborncore.common.network.packet.RebornPackets;
import reborncore.common.registration.IRegistryFactory;
import reborncore.common.registration.RegistrationManager;
import reborncore.common.registration.RegistryTarget;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

@IRegistryFactory.RegistryFactory
public class PacketRegistryFactory implements IRegistryFactory
{

	@Override
	public Class<? extends Annotation> getAnnotation()
	{
		return PacketRegistry.class;
	}

	@Override
	public void handleClass(Class clazz)
	{
		PacketRegistry packetRegistry = (PacketRegistry) RegistrationManager.getAnnoationFromArray(clazz.getAnnotations(), this);
		RebornPackets.packetList.add(Pair.of(packetRegistry.proccessingSide(), clazz));
	}

	@Override
	public List<RegistryTarget> getTargets()
	{
		return Collections.singletonList(RegistryTarget.CLASS);
	}
}
