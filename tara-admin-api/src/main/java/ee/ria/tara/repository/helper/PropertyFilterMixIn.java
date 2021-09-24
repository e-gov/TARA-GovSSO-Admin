package ee.ria.tara.repository.helper;

import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("custom_serializer")
public class PropertyFilterMixIn {}