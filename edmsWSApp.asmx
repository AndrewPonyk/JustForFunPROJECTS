<?xml version="1.0" encoding="utf-8"?>
<wsdl:definitions xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:tm="http://microsoft.com/wsdl/mime/textMatching/" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" xmlns:mime="http://schemas.xmlsoap.org/wsdl/mime/" xmlns:tns="http://localhost/" xmlns:s="http://www.w3.org/2001/XMLSchema" xmlns:soap12="http://schemas.xmlsoap.org/wsdl/soap12/" xmlns:http="http://schemas.xmlsoap.org/wsdl/http/" targetNamespace="http://localhost/" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/">
  <wsdl:types>
    <s:schema elementFormDefault="qualified" targetNamespace="http://localhost/">
      <s:import namespace="http://schemas.xmlsoap.org/soap/encoding/" />
      <s:import namespace="http://schemas.xmlsoap.org/wsdl/" />
      <s:complexType name="edmsResponse">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" form="unqualified" name="code" type="s:long" />
          <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="message" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="edmsDocument">
        <s:complexContent mixed="false">
          <s:extension base="tns:edmsProfiledObject">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="propVersions" type="tns:ArrayOfEdmsVersion" />
              <s:element minOccurs="1" maxOccurs="1" form="unqualified" name="propIdxHighestVersion" type="s:long" />
              <s:element minOccurs="1" maxOccurs="1" form="unqualified" name="propIdxMostRecentVersion" type="s:long" />
              <s:element minOccurs="1" maxOccurs="1" form="unqualified" name="propIdxHighestPublishedVersion" type="s:long" />
              <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="propFolderVersionType" type="s:string" />
              <s:element minOccurs="1" maxOccurs="1" form="unqualified" name="propFolderVersion" type="s:long" />
              <s:element minOccurs="1" maxOccurs="1" form="unqualified" name="propIdxSuggestedVersion" type="s:long" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="edmsProfiledObject">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="propColumns" type="tns:ArrayOfEdmsColumn" />
          <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="propFormName" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" form="unqualified" name="propDocNumber" type="s:long" />
          <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="propLibrary" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="propFolderDisplayName" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfEdmsColumn">
        <s:complexContent mixed="false">
          <s:restriction base="soapenc:Array">
            <s:attribute wsdl:arrayType="tns:edmsColumn[]" ref="soapenc:arrayType" />
          </s:restriction>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="edmsColumn">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="propName" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="propValue" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfEdmsVersion">
        <s:complexContent mixed="false">
          <s:restriction base="soapenc:Array">
            <s:attribute wsdl:arrayType="tns:edmsVersion[]" ref="soapenc:arrayType" />
          </s:restriction>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="edmsVersion">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="propLabel" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" form="unqualified" name="propSize" type="s:long" />
          <s:element minOccurs="1" maxOccurs="1" form="unqualified" name="propVersionID" type="s:long" />
          <s:element minOccurs="1" maxOccurs="1" form="unqualified" name="propLastEditDate" type="s:dateTime" />
          <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="propVersion" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="propSubversion" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" form="unqualified" name="propPublished" type="s:boolean" />
          <s:element minOccurs="1" maxOccurs="1" form="unqualified" name="propAttachment" type="s:boolean" />
          <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="propMIMEType" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="propExtension" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="propSuggestedFileName" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="edmsSearchResults">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="propContents" type="tns:ArrayOfEdmsItem" />
          <s:element minOccurs="1" maxOccurs="1" form="unqualified" name="propItemCount" type="s:long" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfEdmsItem">
        <s:complexContent mixed="false">
          <s:restriction base="soapenc:Array">
            <s:attribute wsdl:arrayType="tns:edmsItem[]" ref="soapenc:arrayType" />
          </s:restriction>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="edmsItem">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="propItemType" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="propItem" type="s:anyType" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfEdmsTrustee">
        <s:complexContent mixed="false">
          <s:restriction base="soapenc:Array">
            <s:attribute wsdl:arrayType="tns:edmsTrustee[]" ref="soapenc:arrayType" />
          </s:restriction>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="edmsTrustee">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="propTrusteeID" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="propTrusteeType" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" form="unqualified" name="propAccessRights" type="s:int" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="edmsFolder">
        <s:complexContent mixed="false">
          <s:extension base="tns:edmsProfiledObject">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="propContents" type="tns:ArrayOfEdmsItem" />
              <s:element minOccurs="1" maxOccurs="1" form="unqualified" name="propItemCount" type="s:long" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
    </s:schema>
  </wsdl:types>
  <wsdl:message name="retrieveFileSizesForEdmsDocumentsSoapIn">
    <wsdl:part name="retrieve" type="s:boolean" />
  </wsdl:message>
  <wsdl:message name="retrieveFileSizesForEdmsDocumentsSoapOut">
    <wsdl:part name="retrieveFileSizesForEdmsDocumentsResult" type="s:string" />
  </wsdl:message>
  <wsdl:message name="loginAsGuestSoapIn">
    <wsdl:part name="clientVersion" type="s:string" />
    <wsdl:part name="dst" type="s:string" />
  </wsdl:message>
  <wsdl:message name="loginAsGuestSoapOut">
    <wsdl:part name="loginAsGuestResult" type="tns:edmsResponse" />
    <wsdl:part name="dst" type="s:string" />
  </wsdl:message>
  <wsdl:message name="getDocumentByIdentifierSoapIn">
    <wsdl:part name="dst" type="s:string" />
    <wsdl:part name="libLibrary" type="s:string" />
    <wsdl:part name="dnDocumentNumber" type="s:long" />
    <wsdl:part name="alternateDefaultFormName" type="s:string" />
  </wsdl:message>
  <wsdl:message name="getDocumentByIdentifierSoapOut">
    <wsdl:part name="getDocumentByIdentifierResult" type="tns:edmsDocument" />
  </wsdl:message>
  <wsdl:message name="getSearchResultsSoapIn">
    <wsdl:part name="dst" type="s:string" />
    <wsdl:part name="libLibrary" type="s:string" />
    <wsdl:part name="searchCriteria" type="tns:ArrayOfEdmsColumn" />
    <wsdl:part name="sortOrder" type="tns:ArrayOfEdmsColumn" />
    <wsdl:part name="maxResults" type="s:long" />
    <wsdl:part name="alternateDefaultFormName" type="s:string" />
  </wsdl:message>
  <wsdl:message name="getSearchResultsSoapOut">
    <wsdl:part name="getSearchResultsResult" type="tns:edmsSearchResults" />
  </wsdl:message>
  <wsdl:message name="getFileBlockSoapIn">
    <wsdl:part name="dst" type="s:string" />
    <wsdl:part name="libLibrary" type="s:string" />
    <wsdl:part name="dnDocumentNumber" type="s:long" />
    <wsdl:part name="vidVersionID" type="s:long" />
    <wsdl:part name="blockSize" type="s:long" />
    <wsdl:part name="blockNumber" type="s:long" />
  </wsdl:message>
  <wsdl:message name="getFileBlockSoapOut">
    <wsdl:part name="getFileBlockResult" type="s:base64Binary" />
  </wsdl:message>
  <wsdl:message name="loginAsUserSoapIn">
    <wsdl:part name="library" type="s:string" />
    <wsdl:part name="loginType" type="s:int" />
    <wsdl:part name="userName" type="s:string" />
    <wsdl:part name="userPassword" type="s:string" />
    <wsdl:part name="network" type="s:string" />
    <wsdl:part name="clientVersion" type="s:string" />
    <wsdl:part name="dst" type="s:string" />
  </wsdl:message>
  <wsdl:message name="loginAsUserSoapOut">
    <wsdl:part name="loginAsUserResult" type="tns:edmsResponse" />
    <wsdl:part name="dst" type="s:string" />
  </wsdl:message>
  <wsdl:message name="versionSoapIn" />
  <wsdl:message name="versionSoapOut">
    <wsdl:part name="versionResult" type="s:string" />
  </wsdl:message>
  <wsdl:message name="streamFileForUploadSoapIn">
    <wsdl:part name="dst" type="s:string" />
    <wsdl:part name="uploadID" type="s:int" />
    <wsdl:part name="documentContents" type="s:base64Binary" />
  </wsdl:message>
  <wsdl:message name="streamFileForUploadSoapOut">
    <wsdl:part name="streamFileForUploadResult" type="tns:edmsResponse" />
    <wsdl:part name="uploadID" type="s:int" />
  </wsdl:message>
  <wsdl:message name="createDocumentExSoapIn">
    <wsdl:part name="dst" type="s:string" />
    <wsdl:part name="library" type="s:string" />
    <wsdl:part name="profileForm" type="s:string" />
    <wsdl:part name="properties" type="tns:ArrayOfEdmsColumn" />
    <wsdl:part name="testCreation" type="s:boolean" />
    <wsdl:part name="fileExtension" type="s:string" />
    <wsdl:part name="documentStreamed" type="s:boolean" />
    <wsdl:part name="uploadID" type="s:int" />
    <wsdl:part name="documentContents" type="s:base64Binary" />
    <wsdl:part name="trustees" type="tns:ArrayOfEdmsTrustee" />
    <wsdl:part name="dnDocumentNumber" type="s:long" />
    <wsdl:part name="vidVersionID" type="s:long" />
  </wsdl:message>
  <wsdl:message name="createDocumentExSoapOut">
    <wsdl:part name="createDocumentExResult" type="tns:edmsResponse" />
    <wsdl:part name="dnDocumentNumber" type="s:long" />
    <wsdl:part name="vidVersionID" type="s:long" />
  </wsdl:message>
  <wsdl:message name="createDocumentSoapIn">
    <wsdl:part name="dst" type="s:string" />
    <wsdl:part name="library" type="s:string" />
    <wsdl:part name="profileForm" type="s:string" />
    <wsdl:part name="properties" type="tns:ArrayOfEdmsColumn" />
    <wsdl:part name="documentContents" type="s:base64Binary" />
    <wsdl:part name="trustees" type="tns:ArrayOfEdmsTrustee" />
    <wsdl:part name="dnDocumentNumber" type="s:long" />
    <wsdl:part name="vidVersionID" type="s:long" />
  </wsdl:message>
  <wsdl:message name="createDocumentSoapOut">
    <wsdl:part name="createDocumentResult" type="tns:edmsResponse" />
    <wsdl:part name="dnDocumentNumber" type="s:long" />
    <wsdl:part name="vidVersionID" type="s:long" />
  </wsdl:message>
  <wsdl:message name="getFolderByIdentifierSoapIn">
    <wsdl:part name="dst" type="s:string" />
    <wsdl:part name="libLibrary" type="s:string" />
    <wsdl:part name="dnDocumentNumber" type="s:long" />
    <wsdl:part name="alternateDefaultFormName" type="s:string" />
    <wsdl:part name="boolRetrieveFolderContents" type="s:boolean" />
  </wsdl:message>
  <wsdl:message name="getFolderByIdentifierSoapOut">
    <wsdl:part name="getFolderByIdentifierResult" type="tns:edmsFolder" />
  </wsdl:message>
  <wsdl:portType name="edmsWSAppSoap">
    <wsdl:operation name="retrieveFileSizesForEdmsDocuments">
      <wsdl:input message="tns:retrieveFileSizesForEdmsDocumentsSoapIn" />
      <wsdl:output message="tns:retrieveFileSizesForEdmsDocumentsSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="loginAsGuest" parameterOrder="clientVersion dst">
      <wsdl:input message="tns:loginAsGuestSoapIn" />
      <wsdl:output message="tns:loginAsGuestSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="getDocumentByIdentifier">
      <wsdl:input message="tns:getDocumentByIdentifierSoapIn" />
      <wsdl:output message="tns:getDocumentByIdentifierSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="getSearchResults">
      <wsdl:input message="tns:getSearchResultsSoapIn" />
      <wsdl:output message="tns:getSearchResultsSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="getFileBlock">
      <wsdl:input message="tns:getFileBlockSoapIn" />
      <wsdl:output message="tns:getFileBlockSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="loginAsUser" parameterOrder="library loginType userName userPassword network clientVersion dst">
      <wsdl:input message="tns:loginAsUserSoapIn" />
      <wsdl:output message="tns:loginAsUserSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="version">
      <wsdl:input message="tns:versionSoapIn" />
      <wsdl:output message="tns:versionSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="streamFileForUpload" parameterOrder="dst uploadID documentContents">
      <wsdl:input message="tns:streamFileForUploadSoapIn" />
      <wsdl:output message="tns:streamFileForUploadSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="createDocumentEx" parameterOrder="dst library profileForm properties testCreation fileExtension documentStreamed uploadID documentContents trustees dnDocumentNumber vidVersionID">
      <wsdl:input message="tns:createDocumentExSoapIn" />
      <wsdl:output message="tns:createDocumentExSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="createDocument" parameterOrder="dst library profileForm properties documentContents trustees dnDocumentNumber vidVersionID">
      <wsdl:input message="tns:createDocumentSoapIn" />
      <wsdl:output message="tns:createDocumentSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="getFolderByIdentifier">
      <wsdl:input message="tns:getFolderByIdentifierSoapIn" />
      <wsdl:output message="tns:getFolderByIdentifierSoapOut" />
    </wsdl:operation>
  </wsdl:portType>
  <wsdl:binding name="edmsWSAppSoap" type="tns:edmsWSAppSoap">
    <soap:binding transport="http://schemas.xmlsoap.org/soap/http" style="rpc" />
    <wsdl:operation name="retrieveFileSizesForEdmsDocuments">
      <soap:operation soapAction="http://localhost/retrieveFileSizesForEdmsDocuments" style="rpc" />
      <wsdl:input>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="loginAsGuest">
      <soap:operation soapAction="http://localhost/loginAsGuest" style="rpc" />
      <wsdl:input>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="getDocumentByIdentifier">
      <soap:operation soapAction="http://localhost/getDocumentByIdentifier" style="rpc" />
      <wsdl:input>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="getSearchResults">
      <soap:operation soapAction="http://localhost/getSearchResults" style="rpc" />
      <wsdl:input>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="getFileBlock">
      <soap:operation soapAction="http://localhost/getFileBlock" style="rpc" />
      <wsdl:input>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="loginAsUser">
      <soap:operation soapAction="http://localhost/loginAsUser" style="rpc" />
      <wsdl:input>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="version">
      <soap:operation soapAction="http://localhost/version" style="rpc" />
      <wsdl:input>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="streamFileForUpload">
      <soap:operation soapAction="http://localhost/streamFileForUpload" style="rpc" />
      <wsdl:input>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="createDocumentEx">
      <soap:operation soapAction="http://localhost/createDocumentEx" style="rpc" />
      <wsdl:input>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="createDocument">
      <soap:operation soapAction="http://localhost/createDocument" style="rpc" />
      <wsdl:input>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="getFolderByIdentifier">
      <soap:operation soapAction="http://localhost/getFolderByIdentifier" style="rpc" />
      <wsdl:input>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="encoded" namespace="http://localhost/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:output>
    </wsdl:operation>
  </wsdl:binding>
  <wsdl:binding name="edmsWSAppSoap12" type="tns:edmsWSAppSoap">
    <soap12:binding transport="http://schemas.xmlsoap.org/soap/http" style="rpc" />
    <wsdl:operation name="retrieveFileSizesForEdmsDocuments">
      <soap12:operation soapAction="http://localhost/retrieveFileSizesForEdmsDocuments" style="rpc" />
      <wsdl:input>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="loginAsGuest">
      <soap12:operation soapAction="http://localhost/loginAsGuest" style="rpc" />
      <wsdl:input>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="getDocumentByIdentifier">
      <soap12:operation soapAction="http://localhost/getDocumentByIdentifier" style="rpc" />
      <wsdl:input>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="getSearchResults">
      <soap12:operation soapAction="http://localhost/getSearchResults" style="rpc" />
      <wsdl:input>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="getFileBlock">
      <soap12:operation soapAction="http://localhost/getFileBlock" style="rpc" />
      <wsdl:input>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="loginAsUser">
      <soap12:operation soapAction="http://localhost/loginAsUser" style="rpc" />
      <wsdl:input>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="version">
      <soap12:operation soapAction="http://localhost/version" style="rpc" />
      <wsdl:input>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="streamFileForUpload">
      <soap12:operation soapAction="http://localhost/streamFileForUpload" style="rpc" />
      <wsdl:input>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="createDocumentEx">
      <soap12:operation soapAction="http://localhost/createDocumentEx" style="rpc" />
      <wsdl:input>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="createDocument">
      <soap12:operation soapAction="http://localhost/createDocument" style="rpc" />
      <wsdl:input>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="getFolderByIdentifier">
      <soap12:operation soapAction="http://localhost/getFolderByIdentifier" style="rpc" />
      <wsdl:input>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="encoded" namespace="http://localhost/" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:output>
    </wsdl:operation>
  </wsdl:binding>
  <wsdl:service name="edmsWSApp">
    <wsdl:port name="edmsWSAppSoap" binding="tns:edmsWSAppSoap">
      <soap:address location="http://corosits8sap753/edmsWS/edmsWSApp.asmx" />
    </wsdl:port>
    <wsdl:port name="edmsWSAppSoap12" binding="tns:edmsWSAppSoap12">
      <soap12:address location="http://corosits8sap753/edmsWS/edmsWSApp.asmx" />
    </wsdl:port>
  </wsdl:service>
</wsdl:definitions>