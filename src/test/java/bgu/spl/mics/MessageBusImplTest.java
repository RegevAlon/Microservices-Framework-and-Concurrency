package bgu.spl.mics;

class MessageBusImplTest {
/*
    MessageBus mb = MessageBusImpl.getInstance();
    MicroService m1 = new StudentService("check1");
    MicroService m2 = new StudentService("check2");
    ExampleEvent event = new ExampleEvent("regev");
    ExampleBroadcast broadcast = new ExampleBroadcast("regev");
    Callback<String> callback;

    @BeforeEach
    void setUp() {
        StudentService m1 = new StudentService("hi");
        m1 = new StudentService("check1");
        m2 = new StudentService("check2");
        event = new ExampleEvent("regev");
        broadcast = new ExampleBroadcast("regev");
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void subscribeBroadcast() {
        setUp();
        mb.register(m1);
        mb.subscribeBroadcast(ExampleBroadcast.class,m1);
        Boolean res = mb.subsIsempty();
        assertFalse(res);

    }

    @Test
    void sendBroadcast() {
        Broadcast b = new ExampleBroadcast("0");
        mb.register(m1);
        mb.register(m2);
        mb.subscribeBroadcast(b.getClass(),m1);
        m2.sendBroadcast(b);
        assertFalse(mb.statIsempty());
    }

    @Test
    void register() {
        mb.register(m1);
        Boolean res = mb.msgQIsempty();
        assertFalse(res);
    }

    @Test
    void unregister() {
        mb.register(m1);
        mb.unregister(m1);
        Boolean res = mb.msgQIsempty();
        assertFalse(res);
    }

    @Test
    void awaitMessage() {
        setUp();
        String expect = "Microservice was never registered";
        mb.register(m1);
        mb.subscribeBroadcast(ExampleBroadcast.class, m1);
    }

 */
}