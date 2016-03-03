package hzg.wpn.predator.web;

import hzg.wpn.properties.Property;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 3/3/16
 */
public class LoginProperties {
    @Property("predator.tomcat.use.kerberos")
    public boolean isKerberos;

    @Property("predator.tomcat.user.name")
    public String tomcatUserName;
    @Property("predator.tomcat.user.pass")
    public String tomcatUserPassword;

    @Property("java.security.krb5.realm")
    public String kerberosRealm;

    @Property("java.security.krb5.kdc")
    public String kerberosKdc;

}
