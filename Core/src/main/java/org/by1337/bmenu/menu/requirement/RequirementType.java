package org.by1337.bmenu.menu.requirement;

import org.by1337.blib.configuration.YamlContext;
import org.by1337.blib.nbt.impl.CompoundTag;
import org.by1337.blib.util.collection.ImmutableArrayList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public enum RequirementType {
    MATH("math", MathRequirement::new, MathRequirement::new, Collections.singletonList("m")),
    STRING_EQUALS("string equals", StringEqualsRequirement::new, StringEqualsRequirement::new, Collections.singletonList("se")),
    STRING_EQUALS_IGNORE_CASE("string equals ignorecase", StringEqualsIgnoreCaseRequirement::new, StringEqualsIgnoreCaseRequirement::new, Collections.singletonList("sei")),
    STRING_CONTAINS("string contains", StringContainsRequirement::new, StringContainsRequirement::new, Collections.singletonList("sc")),
    REGEX_MATCHES_REQUIREMENT("regex matches", RegexMatchesRequirement::new, RegexMatchesRequirement::new, Collections.singletonList("rm")),
    HAS_PERMISSION("has permission", HasPermisionRequirement::new, HasPermisionRequirement::new, Collections.singletonList("hp")),
    ;
    public final String id;
    public final Function<YamlContext, Requirement> fromYaml;
    public final Function<CompoundTag, Requirement> fromNbt;
    public final ImmutableArrayList<String> aliases;

    RequirementType(String id, Function<YamlContext, Requirement> fromYaml, Function<CompoundTag, Requirement> fromNbt) {
        this.id = id;
        this.fromYaml = fromYaml;
        this.fromNbt = fromNbt;
        aliases = new ImmutableArrayList<>(new ArrayList<>());
    }

    RequirementType(String id, Function<YamlContext, Requirement> fromYaml, Function<CompoundTag, Requirement> fromNbt, List<String> aliases) {
        this.id = id;
        this.fromYaml = fromYaml;
        this.fromNbt = fromNbt;
        this.aliases = new ImmutableArrayList<>(aliases);
    }

    @Nullable
    public static RequirementType byName(String name) {
        String id;
        if (name.charAt(0) == '!') {
            id = name.substring(1);
        } else {
            id = name;
        }
        for (RequirementType type : values()) {
            if (type.id.equals(id) || type.aliases.contains(id)){
                return type;
            }
        }
        return null;
    }
}
