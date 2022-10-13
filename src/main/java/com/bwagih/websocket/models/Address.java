package com.bwagih.websocket.models;


import java.io.Serializable;


public class Address implements Serializable {

    private static final long serialVersionUID = 4924045450477798779L;

    private String street;

    private Integer number;

    public Address() {}


    public Address(String street, int number) {
        super();
        this.street = street;
        this.number = number;
    }


    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }


    public Integer getNumber() {
        return number;
    }


    public void setNumber(Integer number) {
        this.number = number;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((number == null) ? 0 : number.hashCode());
        result = prime * result + ((street == null) ? 0 : street.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Address))
            return false;
        Address other = (Address) obj;
        if (number == null) {
            if (other.number != null)
                return false;
        } else if (!number.equals(other.number))
            return false;
        if (street == null) {
            if (other.street != null)
                return false;
        } else if (!street.equals(other.street))
            return false;
        return true;
    }
}
