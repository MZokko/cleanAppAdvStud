package com.example.cleanapp.Model;

public class HouseInvitationModel {

    //owner info
    String idHouse,idOwner,ownerPhone;

    //tenant info
    String idTenant , mailTenant,tenantPhone;
    //boolean read
    Boolean read=false;

    String messageInvitation;

    //constructor


    public HouseInvitationModel() {
    }

    //GETTER SETTER


    public String getOwnerPhone() {
        return ownerPhone;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

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

    public String getTenantPhone() {
        return tenantPhone;
    }

    public void setTenantPhone(String tenantPhone) {
        this.tenantPhone = tenantPhone;
    }

    public String getMessageInvitation() {
        return messageInvitation;
    }

    public void setMessageInvitation(String messageInvitation) {
        this.messageInvitation = messageInvitation;
    }
}
