package io.jenkins.plugins.orka;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.jvnet.hudson.test.JenkinsRule;

import hudson.util.ListBoxModel;
import io.jenkins.plugins.orka.client.OrkaClient;
import io.jenkins.plugins.orka.client.VMResponse;
import io.jenkins.plugins.orka.helpers.ClientFactory;

@RunWith(Parameterized.class)
public class VMItemsFillTest {
    @ClassRule
    public static JenkinsRule r = new JenkinsRule();

    @Parameterized.Parameters(name = "{index}: Test with createNewConfig={0}, endpoint={1}, credentials={2}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
                new Object[][] { { false, "endpoint", "credentials", 2 }, { true, "endpoint", "credentials", 0 },
                        { false, null, "credentials", 0 }, { false, "endpoint", null, 0 }, });
    }

    private ClientFactory factory;
    private final boolean createNewConfig;
    private final String endpoint;
    private final String credentials;
    private final int resultSize;

    public VMItemsFillTest(boolean createNewConfig, String endpoint, String credentials, int resultSize) {
        this.createNewConfig = createNewConfig;
        this.endpoint = endpoint;
        this.credentials = credentials;
        this.resultSize = resultSize;
    }

    @Before
    public void initialize() throws IOException {
        VMResponse firstVM = new VMResponse("first", "deployed", 12, "Mojave.img", "firstImage", "default");
        VMResponse secondVM = new VMResponse("second", "not deployed", 24, "Mojave.img", "secondImage", "default");
        List<VMResponse> response = Arrays.asList(firstVM, secondVM);
        OrkaClient client = mock(OrkaClient.class);

        this.factory = mock(ClientFactory.class);
        when(factory.getOrkaClient(anyString(), anyString())).thenReturn(client);
        when(client.getVMs()).thenReturn(response);
    }

    @Test
    public void when_fill_vm_items_in_orka_agent_should_return_correct_vm_size() throws IOException {
        OrkaAgent.DescriptorImpl descriptor = new OrkaAgent.DescriptorImpl();
        descriptor.setClientFactory(this.factory);

        ListBoxModel vms = descriptor.doFillVmItems(this.endpoint, this.credentials, this.createNewConfig);

        assertEquals(this.resultSize, vms.size());
    }

    @Test
    public void when_fill_vm_items_in_agent_template_should_return_correct_vm_size() throws IOException {
        AgentTemplate.DescriptorImpl descriptor = new AgentTemplate.DescriptorImpl();
        descriptor.setClientFactory(this.factory);

        ListBoxModel vms = descriptor.doFillVmItems(this.endpoint, this.credentials, this.createNewConfig);

        assertEquals(this.resultSize, vms.size());
    }
}