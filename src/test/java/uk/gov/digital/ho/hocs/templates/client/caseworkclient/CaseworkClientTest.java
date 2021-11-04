package uk.gov.digital.ho.hocs.templates.client.caseworkclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.digital.ho.hocs.templates.application.RestHelper;
import uk.gov.digital.ho.hocs.templates.client.caseworkclient.dto.AddressDto;
import uk.gov.digital.ho.hocs.templates.client.caseworkclient.dto.CaseDataDto;
import uk.gov.digital.ho.hocs.templates.client.caseworkclient.dto.CorrespondentDto;
import uk.gov.digital.ho.hocs.templates.client.caseworkclient.dto.CorrespondentsDto;
import uk.gov.digital.ho.hocs.templates.domain.exception.ApplicationExceptions;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CaseworkClientTest {

    @Mock
    RestHelper restHelper;
    private static final String MIN = "MIN";
    private final UUID CASE_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID PRIMARY_CORRESPONDENT_UUID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private final String serviceBaseURL = "http://localhost:8082";
    private final UUID uuid = UUID.randomUUID();

    CaseworkClient caseworkClient;

    @BeforeEach
    public void setUp() {
        this.caseworkClient = new CaseworkClient(restHelper, serviceBaseURL);
    }

    @Test
    public void shouldGetCaseData() {
        CaseDataDto caseDataDto = new CaseDataDto();

        when(restHelper.get(eq(serviceBaseURL), eq(String.format("/case/%s", uuid)), eq(CaseDataDto.class))).thenReturn(caseDataDto);

        caseworkClient.getCase(uuid);

        verify(restHelper, times(1)).get(eq(serviceBaseURL), eq(String.format("/case/%s", uuid)), eq(CaseDataDto.class));
        verifyNoMoreInteractions(restHelper);

    }

    @Test
    public void shouldGetCorrespondentsData() {

        when(restHelper.get(eq(serviceBaseURL), eq(String.format("/case/%s/correspondent", uuid)), eq(CorrespondentsDto.class))).thenReturn(getCorrespondents());

        caseworkClient.getCorrespondents(uuid);

        verify(restHelper, times(1)).get(eq(serviceBaseURL), eq(String.format("/case/%s/correspondent", uuid)), eq(CorrespondentsDto.class));
        verifyNoMoreInteractions(restHelper);
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhenGetCaseData() {

        doThrow(HttpClientErrorException.NotFound.class).when(restHelper).get(eq(serviceBaseURL), eq(String.format("/case/%s", uuid)), eq(CaseDataDto.class));
        assertThrows(HttpClientErrorException.NotFound.class, () -> caseworkClient.getCase(uuid));

        verify(restHelper, times(1)).get(eq(serviceBaseURL), eq(String.format("/case/%s", uuid)), eq(CaseDataDto.class));
        verifyNoMoreInteractions(restHelper);

    }

    @Test
    public void shouldTHrowEntityNotFoundExceptionWhenGetCorrespondentsData() {
        CorrespondentsDto correspondents = new CorrespondentsDto(new HashSet<>());

        when(restHelper.get(eq(serviceBaseURL), eq(String.format("/case/%s/correspondent", uuid)), eq(CorrespondentsDto.class))).thenReturn(correspondents);

        assertThrows(ApplicationExceptions.EntityNotFoundException.class, () -> caseworkClient.getCorrespondents(uuid));
        ;

        verify(restHelper, times(1)).get(eq(serviceBaseURL), eq(String.format("/case/%s/correspondent", uuid)), eq(CorrespondentsDto.class));
        verifyNoMoreInteractions(restHelper);
    }

    private CorrespondentsDto getCorrespondents() {
        AddressDto address = new AddressDto("S1 1DJ", "1 Somewhere Street", "Somewhere", "", "");
        AddressDto address1 = new AddressDto("N1 2XR", "2 Elsewhere Road", "AnyWhere", "North", "");
        CorrespondentDto primaryCorrespondent = new CorrespondentDto(PRIMARY_CORRESPONDENT_UUID, LocalDateTime.now(), "MEMBER", CASE_UUID, "Bob Smith MP", address, "", "", "ref1");
        CorrespondentDto constituent = new CorrespondentDto(UUID.fromString("66666666-6666-6666-6666-666666666666"), LocalDateTime.now(), "CONSTITUENT", CASE_UUID, "Jon", address1, "", "", "");
        return new CorrespondentsDto(Stream.of(primaryCorrespondent, constituent).collect(Collectors.toCollection(HashSet::new)));

    }
}


