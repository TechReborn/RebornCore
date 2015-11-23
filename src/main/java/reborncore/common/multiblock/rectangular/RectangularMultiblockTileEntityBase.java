package reborncore.common.multiblock.rectangular;


import net.minecraft.util.EnumFacing;
import reborncore.common.multiblock.CoordTriplet;
import reborncore.common.multiblock.MultiblockControllerBase;
import reborncore.common.multiblock.MultiblockTileEntityBase;
import reborncore.common.multiblock.MultiblockValidationException;

public abstract class RectangularMultiblockTileEntityBase extends
        MultiblockTileEntityBase {

    PartPosition position;
    EnumFacing outwards;

    public RectangularMultiblockTileEntityBase() {
        super();

        position = PartPosition.Unknown;
        outwards = null;
    }

    // Positional Data
    public EnumFacing getOutwardsDir() {
        return outwards;
    }

    public PartPosition getPartPosition() {
        return position;
    }

    // Handlers from MultiblockTileEntityBase
    @Override
    public void onAttached(MultiblockControllerBase newController) {
        super.onAttached(newController);
        recalculateOutwardsDirection(newController.getMinimumCoord(),
                newController.getMaximumCoord());
    }

    @Override
    public void onMachineAssembled(MultiblockControllerBase controller) {
        CoordTriplet maxCoord = controller.getMaximumCoord();
        CoordTriplet minCoord = controller.getMinimumCoord();

        // Discover where I am on the reactor
        recalculateOutwardsDirection(minCoord, maxCoord);
    }

    @Override
    public void onMachineBroken() {
        position = PartPosition.Unknown;
        outwards = null;
    }

    // Positional helpers
    public void recalculateOutwardsDirection(CoordTriplet minCoord,
                                             CoordTriplet maxCoord) {
        outwards = null;
        position = PartPosition.Unknown;

        int facesMatching = 0;
        if (maxCoord.x == this.getPos().getX() || minCoord.x == this.getPos().getX() ) {
            facesMatching++;
        }
        if (maxCoord.y == this.getPos().getY()  || minCoord.y == this.getPos().getY()) {
            facesMatching++;
        }
        if (maxCoord.z == this.getPos().getZ() || minCoord.z == this.getPos().getZ()) {
            facesMatching++;
        }

        if (facesMatching <= 0) {
            position = PartPosition.Interior;
        } else if (facesMatching >= 3) {
            position = PartPosition.FrameCorner;
        } else if (facesMatching == 2) {
            position = PartPosition.Frame;
        } else {
            // 1 face matches
            if (maxCoord.x == this.getPos().getX() ) {
                position = PartPosition.EastFace;
                outwards = EnumFacing.EAST;
            } else if (minCoord.x == this.getPos().getX() ) {
                position = PartPosition.WestFace;
                outwards = EnumFacing.WEST;
            } else if (maxCoord.z == this.getPos().getZ()) {
                position = PartPosition.SouthFace;
                outwards = EnumFacing.SOUTH;
            } else if (minCoord.z == this.getPos().getZ()) {
                position = PartPosition.NorthFace;
                outwards = EnumFacing.NORTH;
            } else if (maxCoord.y == this.getPos().getY()) {
                position = PartPosition.TopFace;
                outwards = EnumFacing.UP;
            } else {
                position = PartPosition.BottomFace;
                outwards = EnumFacing.DOWN;
            }
        }
    }

    // /// Validation Helpers (IMultiblockPart)
    public abstract void isGoodForFrame() throws MultiblockValidationException;

    public abstract void isGoodForSides() throws MultiblockValidationException;

    public abstract void isGoodForTop() throws MultiblockValidationException;

    public abstract void isGoodForBottom() throws MultiblockValidationException;

    public abstract void isGoodForInterior()
            throws MultiblockValidationException;
}
