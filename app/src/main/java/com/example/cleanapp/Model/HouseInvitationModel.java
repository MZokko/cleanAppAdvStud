package com.example.cleanapp.Model;

public class HouseInvitationModel {

    //owner info
    String idHouse,idOwner;
    int ownerPhone;

    //tenant info
    String idTenant , mailTenant;
    int tenantPhone;

    String messageInvitation;

    //constructor


    public HouseInvitationModel() {
    }

    //GETTER SETTER


    public String getIdHouse() {
        return idHouse;
    }

    public void setIdHouse(String idHouse) {
        this.idHouse = idHouse;
    }

    public String getIdOwner() {
        return idOwner;
    }

    public void setIdOwner(String idOwner) {
        this.idOwner = idOwner;
    }

    public int getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(int ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getIdTenant() {
        return idTenant;
    }

    public void setIdTenant(String idTenant) {
        this.idTenant = idTenant;
    }

    public String getMailTenant() {
        return mailTenant;
    }

    public void setMailTenant(String mailTenant) {
        this.mailTenant = mailTenant;
    }

    public int getTenantPhone() {
        return tenantPhone;
    }

    public void setTenantPhone(int tenantPhone) {
        this.tenantPhone = tenantPhone;
    }

    public String getMessageInvitation() {
        return messageInvitation;
    }

    public void setMessageInvitation(String messageInvitation) {
        this.messageInvitation = messageInvitation;
    }
}
