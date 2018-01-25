/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.edelivery.smp.data.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static eu.europa.ec.edelivery.smp.data.model.CommonColumnsLengths.MAX_USERNAME_LENGTH;

@Entity
@Table(name = "smp_user")
public class DBUser implements BaseEntity {

    private String username;
    private String password;
    private boolean isAdmin;
    private Set<DBOwnership> ownerships = new HashSet<>();

    public DBUser() {
    }

    @Id
    @Column(name = "username", unique = true, nullable = false, length = MAX_USERNAME_LENGTH)
    public String getUsername() {
        return username;
    }

    @Column(name = "password", length = MAX_USERNAME_LENGTH)
    public String getPassword() {
        return password;
    }


    @Column(name = "isadmin", nullable = false)
    public boolean isAdmin() {
        return isAdmin;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    public Set<DBOwnership> getOwnerships() {
        return ownerships;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setOwnerships(Set<DBOwnership> ownerships) {
        this.ownerships = ownerships;
    }

    @Override
    @Transient
    public String getId() {
        return username;
    }

    public void setId(String username){
        setUsername(username);
    }
}
