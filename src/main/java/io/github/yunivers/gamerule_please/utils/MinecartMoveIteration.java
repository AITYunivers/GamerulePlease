package io.github.yunivers.gamerule_please.utils;

public class MinecartMoveIteration
{
    public double remainingMovement = (double)0.0F;
    public boolean initial = true;
    public boolean slopeVelocityApplied = false;
    public boolean decelerated = false;
    public boolean accelerated = false;

    public boolean shouldContinue() {
        return this.initial || this.remainingMovement > (double)1.0E-5F;
    }
}
