package mctmods.immersivetechnology.api.convergence.capability;

public interface IMechanicalEnergyProvider {
    int getSpeed();

    float getTorque();

    int getMaxSpeed();

    double getBaseMass();

    double getDriveTorque();

    double getFriction();
}
