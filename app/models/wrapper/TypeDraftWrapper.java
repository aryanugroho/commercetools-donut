package models.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.TextInputHint;
import io.sphere.sdk.types.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class TypeDraftWrapper {

    private final String key;
    private final LocalizedString name;
    private final LocalizedString description;
    private final Set<String> resourceTypeIds;
    private final List<FieldDefinitionWrapper> fieldDefinitions;


    public TypeDraftWrapper(@JsonProperty("key") final String key, @JsonProperty("name") final LocalizedString name,
                            @JsonProperty("description") final LocalizedString description,
                            @JsonProperty("resourceTypeIds") final Set<String> resourceTypeIds,
                            @JsonProperty("fieldDefinitions") final List<FieldDefinitionWrapper> fieldDefinitions) {
        this.key = requireNonNull(key);
        this.name = requireNonNull(name);
        this.description = requireNonNull(description);
        this.resourceTypeIds = requireNonNull(resourceTypeIds);
        this.fieldDefinitions = requireNonNull(fieldDefinitions);
    }

    public TypeDraft createTypeDraft() {
        final List<FieldDefinition> fieldDefinitions = getFieldDefinitions().stream()
                .map(fieldDefinitionWrapper -> FieldDefinition.of(mapFieldType(fieldDefinitionWrapper.getType()),
                        fieldDefinitionWrapper.getName(), fieldDefinitionWrapper.getLabel(),
                        fieldDefinitionWrapper.getIsRequired(), mapTextInputHint(fieldDefinitionWrapper.getInputHint())))
                .collect(Collectors.toList());

        final TypeDraft typeDraft = TypeDraftBuilder.of(getKey(), getName(), getResourceTypeIds())
                .description(getDescription()).fieldDefinitions(fieldDefinitions).build();

        return typeDraft;
    }

    private TextInputHint mapTextInputHint(final TextInputHintWrapper textInputHintWrapper) {
        final TextInputHint result;
        switch (textInputHintWrapper) {
            case SINGLE_LINE:
                result = TextInputHint.SINGLE_LINE;
                break;
            case MULTI_LINE:
                result = TextInputHint.MULTI_LINE;
                break;
            default:
                throw new RuntimeException("Unknown TextInputHint: " + textInputHintWrapper);
        }
        return result;
    }

    private FieldType mapFieldType(final AttributeTypeWrapper attributeTypeWrapper) {
        final FieldType result;
        switch (attributeTypeWrapper.name()) {
            case "Number":
                result = NumberType.of();
                break;
            default:
                throw new RuntimeException("Unknown FieldType: " + attributeTypeWrapper.name());
        }
        return result;
    }

    public String getKey() {
        return key;
    }

    public LocalizedString getName() {
        return name;
    }

    public Set<String> getResourceTypeIds() {
        return resourceTypeIds;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public List<FieldDefinitionWrapper> getFieldDefinitions() {
        return fieldDefinitions;
    }
}
