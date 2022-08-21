package Kuboid.manager;

public interface ILogic {

    void init() throws Exception;

    void input() throws Exception;

    void update(float interval, MouseInput mouseInput) throws Exception;

    void render() throws Exception;

    void cleanup();

}
