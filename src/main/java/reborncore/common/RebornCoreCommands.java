package reborncore.common;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.world.chunk.ChunkStatus;
import reborncore.common.crafting.RecipeManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RebornCoreCommands {

	private final static ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

	public static void setup() {
		CommandRegistry.INSTANCE.register(false, RebornCoreCommands::addCommands);
	}

	private static void addCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
				literal("reborncore")

					.then(
						literal("recipes")
							.then(literal("validate")
									.requires(source -> source.hasPermissionLevel(3))
									.executes(ctx -> {
										RecipeManager.validateRecipes(ctx.getSource().getWorld());
										return Command.SINGLE_SUCCESS;
									})
							)
					)

					.then(
						literal("generate")
							.requires(source -> source.hasPermissionLevel(3))
							.then(argument("size", integer())
									.executes(RebornCoreCommands::generate)
							)
					)
		);
	}

	private static int generate(CommandContext<ServerCommandSource> ctx) {
		final int size = getInteger(ctx, "size");

		final ServerWorld world = ctx.getSource().getWorld();
		final ServerChunkManager serverChunkManager = world.getChunkManager();
		final AtomicInteger completed = new AtomicInteger(0);

		for (int x = -(size / 2); x < size / 2; x++) {
			for (int z = -(size / 2); z < size / 2; z++) {
				final int chunkPosX = x;
				final int chunkPosZ = z;
				CompletableFuture.supplyAsync(() -> serverChunkManager.getChunk(chunkPosX, chunkPosZ, ChunkStatus.FULL, true), EXECUTOR_SERVICE)
						.whenComplete((chunk, throwable) -> {
									int max = (int) Math.pow(size, 2);
									ctx.getSource().sendFeedback(new LiteralText(String.format("Finished generating %d:%d (%d/%d %d%%)", chunk.getPos().x, chunk.getPos().z, completed.getAndIncrement(), max, completed.get() == 0 ? 0 : (int) ((completed.get() * 100.0f) / max))), true);
								}
						);
			}
		}
		return Command.SINGLE_SUCCESS;
	}
}