package org.weakref.jmx;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestExports
{
    private MBeanServer server = null;
    private MBeanExporter exporter = null;

    private ObjectName objectName = null;
    private String name = null;

    @BeforeMethod
    public void setUp()
    {
        Assert.assertNull(name);
        objectName = Util.getUniqueObjectName();
        name = objectName.getCanonicalName();
        server = ManagementFactory.getPlatformMBeanServer();
        exporter = new MBeanExporter(server);

        Assert.assertNotNull(server);
        Assert.assertNotNull(exporter);
        Assert.assertNotNull(objectName);
        Assert.assertNotNull(name);
    }

    @AfterMethod
    public void tearDown()
    {
        Assert.assertNotNull(name);
        Assert.assertNotNull(server);
        Assert.assertNotNull(exporter);
        Assert.assertNotNull(objectName);

        exporter.unexport(name);

        name = null;
        server = null;
        exporter = null;
        objectName = null;
    }

    @Test
    public void testExportOk() throws Exception
    {
        exporter.export(name, new TestBean());

        Assert.assertEquals("Hello!", server.getAttribute(objectName, "Hello"));
    }

    @Test
    public void testExportDouble() throws Throwable
    {
        exporter.export(name, new TestBean());

        Assert.assertEquals("Hello!", server.getAttribute(objectName, "Hello"));

        try {
            exporter.export(name, new TestBean());
        }
        catch (JmxException e) {
            Assert.assertEquals(e.getReason(), JmxException.Reason.INSTANCE_ALREADY_EXISTS);
        }
    }

    public static class TestBean
    {
        @Managed
        public String getHello()
        {
            return "Hello!";
        }
    }
}
