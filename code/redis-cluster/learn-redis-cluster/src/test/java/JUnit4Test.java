import org.junit.*;

import static org.junit.Assert.assertEquals;

public class JUnit4Test {
    @Before
    public void before() {
        System.out.println("@Before");
    }

    @Test
    /**
     *Mark your test cases with @Test annotations.
     *You don’t need to prefix your test cases with “test”.
     *tested class does not need to extend from “TestCase” class.
     */
    public void test() {
        System.out.println("@Test");
        assertEquals(5 + 5, 10);
    }

    @Ignore
    @Test
    public void testIgnore() {
        System.out.println("@Ignore");
    }

    @Test(timeout = 50)
    public void testTimeout() {
        System.out.println("@Test(timeout = 50)");
        assertEquals(5 + 5, 10);
    }

    @Test(expected = ArithmeticException.class)
    public void testExpected() {
        System.out.println("@Test(expected = Exception.class)");
        throw new ArithmeticException();
    }

    @After
    public void after() {
        System.out.println("@After");
    }

    @BeforeClass
    public static void beforeClass() {
        System.out.println("@BeforeClass");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("@AfterClass");
    }
}
