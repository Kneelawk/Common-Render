package com.kneelawk.krender.engine.api.util;

import java.util.Arrays;
import java.util.Comparator;

import net.minecraft.core.Direction;

import static net.minecraft.core.Direction.DOWN;
import static net.minecraft.core.Direction.EAST;
import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.SOUTH;
import static net.minecraft.core.Direction.UP;
import static net.minecraft.core.Direction.WEST;
import static net.minecraft.core.Direction.values;

/**
 * Utilities for {@link Direction}s.
 */
public final class DirectionUtils {
    private DirectionUtils() {}

    private static final Direction[][] FACE_SIDES = {
        {NORTH, WEST, SOUTH, EAST},
        {SOUTH, WEST, NORTH, EAST},
        {DOWN, EAST, UP, WEST},
        {DOWN, WEST, UP, EAST},
        {DOWN, NORTH, UP, SOUTH},
        {DOWN, SOUTH, UP, NORTH}
    };

    /**
     * Array of all the horizontal directions, sorted by {@link Direction#get2DDataValue()}.
     */
    public static final Direction[] HORIZONTAL_DIRECTIONS =
        Arrays.stream(values()).filter(d -> d.get2DDataValue() >= 0)
            .sorted(Comparator.comparingInt(Direction::get2DDataValue)).toArray(Direction[]::new);

    /**
     * Gets direction of the side of a face.
     *
     * @param face       the face to get the side of.
     * @param sideOfFace the side of the face to get the direction of.
     * @return the direction of the given side of the given face.
     */
    public static Direction getFaceSide(Direction face, Direction sideOfFace) {
        return FACE_SIDES[face.get3DDataValue()][sideOfFace.get2DDataValue()];
    }
}
