package network.radicle.tools.github.providers;

import com.google.common.base.Strings;
import org.eclipse.microprofile.config.ConfigProvider;
import picocli.CommandLine;

public class ApplicationPropertiesDefaultProvider implements CommandLine.IDefaultValueProvider {
    @Override
    public String defaultValue(CommandLine.Model.ArgSpec argSpec) {
        var defaultValue = argSpec.defaultValue();
        if (!argSpec.isOption()) {
            return defaultValue;
        }

        if (Strings.nullToEmpty(defaultValue).contains(".")) {
            return ConfigProvider.getConfig().getOptionalValue(defaultValue, String.class).orElse(null);
        }

        return defaultValue;
    }
}
