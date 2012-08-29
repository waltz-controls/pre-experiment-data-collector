To set up authentication:
1) add JAAS realm to server.xml

<Realm className="org.apache.catalina.realm.JAASRealm"
                 appName="KommunikationnenPlatform"
                 userClassNames="javax.security.auth.kerberos.KerberosPrincipal"
                 roleClassNames="org.apache.catalina.realm.GenericPrincipal"
                 useContextClassLoader="true"
                 debug="99"/>

2) create {TOMCAT_HOME}/conf/jaas.conf

Tomcat {
  wpn.hdri.auth.BeamtimeLoginModule required debug=true;
};

3) start TomCat with the following variables:

-Djava.security.krb5.realm=WIN.DESY.DE -Djava.security.krb5.kdc=ADC11.WIN.DESY.DE -Djava.security.auth.login.config={TOMCAT_HOME}/conf/jaas.conf

4) add security constraint to the web.xml:

 <security-constraint>
        <web-resource-collection>
            <web-resource-name>eXperiment InformaTion</web-resource-name>
            <url-pattern>/*</url-pattern>
        </web-resource-collection>
        <auth-constraint>
            <role-name>user</role-name>
        </auth-constraint>
    </security-constraint>
 ...
<security-role>
        <description>The owner of the login/password pair.</description>
        <role-name>user</role-name>
    </security-role>

4) attach catalina.jar to the project in pom.xml:

//===============================================
Install TangORB jar manually

 1) {PROJECT_ROOT}>mvn install:install-file -Dfile=lib/TangORB-7.1.1.jar -
    DgroupId=fr.esrf.tango -DartifactId=TangORB -Dversion=7.1.1 -Dpackaging=jar -Dg
    eneratePom=true
