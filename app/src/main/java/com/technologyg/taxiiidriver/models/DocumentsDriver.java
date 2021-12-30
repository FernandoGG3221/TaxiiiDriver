package com.technologyg.taxiiidriver.models;

public class DocumentsDriver {

    String INE;
    String permissionDriver;
    String circulationVehicleTarget;
    String vehicleSafe;
    String RFC;
    String plate;

    //Construct empty
    public DocumentsDriver() {
    }
    //Construct for oficial documents
    public DocumentsDriver(String INE, String RFC, String permissionDriver, String circulationVehicleTarget, String vehicleSafe,  String Plate) {
        this.INE = INE;
        this.permissionDriver = permissionDriver;
        this.circulationVehicleTarget = circulationVehicleTarget;
        this.vehicleSafe = vehicleSafe;
        this.RFC = RFC;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getINE() {
        return INE;
    }

    public void setINE(String INE) {
        this.INE = INE;
    }

    public String getPermissionDriver() {
        return permissionDriver;
    }

    public void setPermissionDriver(String permissionDriver) {
        this.permissionDriver = permissionDriver;
    }

    public String getCirculationVehicleTarget() {
        return circulationVehicleTarget;
    }

    public void setCirculationVehicleTarget(String circulationVehicleTarget) {
        this.circulationVehicleTarget = circulationVehicleTarget;
    }

    public String getVehicleSafe() {
        return vehicleSafe;
    }

    public void setVehicleSafe(String vehicleSafe) {
        this.vehicleSafe = vehicleSafe;
    }

    public String getRFC() {
        return RFC;
    }

    public void setRFC(String RFC) {
        this.RFC = RFC;
    }
}
