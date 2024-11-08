package org.example.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class RoleTest {
    @Test
    public void test_FromString_shoudlParseCorrectly() {
        Role adminRole = Role.fromString("admin");
        assertThat(adminRole, instanceOf(Role.class));
        assertEquals(Long.valueOf(1L), adminRole.id);

        Role gerenteRole = Role.fromString("gerente");
        System.out.println(gerenteRole);
        assertThat(gerenteRole, instanceOf(Role.class));
        assertEquals(Long.valueOf(2L), gerenteRole.id);

        Role operadorRole = Role.fromString("operador");
        System.out.println(operadorRole);
        assertThat(operadorRole, instanceOf(Role.class));
        assertEquals(Long.valueOf(3L), operadorRole.id);
    }
}
