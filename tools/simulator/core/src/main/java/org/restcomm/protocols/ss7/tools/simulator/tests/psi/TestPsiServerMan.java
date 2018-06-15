/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2018, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.restcomm.protocols.ss7.tools.simulator.tests.psi;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParameterFactory;
import org.restcomm.protocols.ss7.map.api.MAPProvider;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage;

import org.restcomm.protocols.ss7.map.MAPParameterFactoryImpl;

import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.LMSI;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdOrLAI;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdFixedLength;
import org.restcomm.protocols.ss7.map.api.primitives.DiameterIdentity;
import org.restcomm.protocols.ss7.map.api.primitives.IMEI;

import org.restcomm.protocols.ss7.map.api.service.lsm.AdditionalNumber;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPServiceMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPServiceMobilityListener;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.AuthenticationFailureReportRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.AuthenticationFailureReportResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.SendAuthenticationInfoRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.SendAuthenticationInfoResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.ForwardCheckSSIndicationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.ResetRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.RestoreDataRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.RestoreDataResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.imei.CheckImeiRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.imei.CheckImeiResponse;

import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.CancelLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.CancelLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SendIdentificationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SendIdentificationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.PurgeMSRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.PurgeMSResponse;

import org.restcomm.protocols.ss7.map.api.service.mobility.oam.ActivateTraceModeRequest_Mobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.oam.ActivateTraceModeResponse_Mobility;

import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeSubscriptionInterrogationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeSubscriptionInterrogationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationGPRS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationEPS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationNumberMap;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberState;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberStateChoice;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.MNPInfoRes;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.GeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.GeodeticInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TypeOfShape;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.NumberPortabilityStatus;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.RouteingNumber;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.NotReachableReason;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.MSClassmark2;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.PSSubscriberState;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.UserCSGInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TAId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.EUtranCgi;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.GPRSMSClass;

import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LSAIdentity;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataResponse;

import org.restcomm.protocols.ss7.map.api.service.sms.SendRoutingInfoForSMRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.SendRoutingInfoForSMResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.MAPDialogSms;
import org.restcomm.protocols.ss7.map.api.service.sms.MAPServiceSms;
import org.restcomm.protocols.ss7.map.api.service.sms.MAPServiceSmsListener;
import org.restcomm.protocols.ss7.map.api.service.sms.LocationInfoWithLMSI;
import org.restcomm.protocols.ss7.map.api.service.sms.ForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.ForwardShortMessageResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.AlertServiceCentreRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.AlertServiceCentreResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.ReportSMDeliveryStatusRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.ReportSMDeliveryStatusResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.MoForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.MoForwardShortMessageResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.MtForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.MtForwardShortMessageResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.InformServiceCentreRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.ReadyForSMRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.ReadyForSMResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.NoteSubscriberPresentRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.IpSmGwGuidance;

import org.restcomm.protocols.ss7.map.primitives.DiameterIdentityImpl;
import org.restcomm.protocols.ss7.map.primitives.IMEIImpl;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;

import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.GeographicalInformationImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.GeodeticInformationImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.RouteingNumberImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.LocationInformationEPSImpl;

import org.restcomm.protocols.ss7.tools.simulator.Stoppable;
import org.restcomm.protocols.ss7.tools.simulator.common.AddressNatureType;
import org.restcomm.protocols.ss7.tools.simulator.common.TesterBase;
import org.restcomm.protocols.ss7.tools.simulator.level3.MapMan;
import org.restcomm.protocols.ss7.tools.simulator.level3.NumberingPlanMapType;
import org.restcomm.protocols.ss7.tools.simulator.management.TesterHostInterface;
import org.restcomm.protocols.ss7.tools.simulator.tests.sms.SRIReaction;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class TestPsiServerMan extends TesterBase implements TestPsiServerManMBean, Stoppable, MAPServiceMobilityListener, MAPServiceSmsListener {

    private static Logger logger = Logger.getLogger(TestPsiServerMan.class);

    public static String SOURCE_NAME = "TestPsiServerMan";
    private final String name;
    private MapMan mapMan;
    private boolean isStarted = false;
    private int countMapSriForSmReq = 0;
    private int countMapSriForSmResp = 0;
    private int countMapPsiReq = 0;
    private int countMapPsiResp = 0;
    private String currentRequestDef = "";
    private MAPProvider mapProvider;
    private MAPServiceMobility mapServiceMobility;
    private MAPServiceSms mapServiceSms;
    private MAPParameterFactory mapParameterFactory;

    public TestPsiServerMan(String name) {
        super(SOURCE_NAME);
        this.name = name;
        this.isStarted = false;
    }

    public boolean start() {

        this.mapProvider = this.mapMan.getMAPStack().getMAPProvider();
        this.mapServiceSms = this.mapProvider.getMAPServiceSms();
        this.mapServiceMobility = mapProvider.getMAPServiceMobility();
        this.mapParameterFactory = mapProvider.getMAPParameterFactory();

        mapServiceSms.acivate();
        mapServiceMobility.acivate();
        mapServiceSms.addMAPServiceListener(this);
        mapServiceMobility.addMAPServiceListener(this);
        mapProvider.addMAPDialogListener(this);

        isStarted = true;
        this.countMapPsiReq = 0;
        this.countMapPsiResp = 0;
        return true;
    }

    public void setTesterHost(TesterHostInterface testerHost) {
        this.testerHost = testerHost;
    }

    public void setMapMan(MapMan val) {
        this.mapMan = val;
    }

    @Override
    public String getState() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append(SOURCE_NAME);
        sb.append(": ");
        sb.append("<br>Count: countMapSriForSmReq-");
        sb.append(countMapSriForSmReq);
        sb.append(", countMapSriForSmResp-");
        sb.append(countMapSriForSmResp);
        sb.append("<br>Count: countMapPsiReq-");
        sb.append(countMapPsiReq);
        sb.append(", countMapPsiResp-");
        sb.append(countMapPsiResp);
        sb.append("</html>");
        return sb.toString();
    }

    @Override
    public void execute() {
    }

    @Override
    public void stop() {
        isStarted = false;
        mapProvider.getMAPServiceLsm().deactivate();
        mapProvider.getMAPServiceMobility().deactivate();
        mapProvider.getMAPServiceSms().removeMAPServiceListener(this);
        mapProvider.getMAPServiceMobility().removeMAPServiceListener(this);
        mapProvider.removeMAPDialogListener(this);
        this.testerHost.sendNotif(SOURCE_NAME, "PSI Client has been stopped", "", Level.INFO);
    }

    //**************************//
    //*** SRIforSMS methods ***//
    //************************//
    @Override
    public String performSendRoutingInfoForSMResponse() {
        if (!isStarted) {
            return "The tester is not started";
        }

        return sendRoutingInfoForSMResponse();
    }

    public String sendRoutingInfoForSMResponse() {

        return "sendRoutingInfoForSMResponse called automatically";
    }

    public void onSendRoutingInfoForSMRequest(SendRoutingInfoForSMRequest sendRoutingInforForSMRequest) {

        MAPErrorMessage mapErrorMessage = null;
        logger.debug("\nonSendRoutingInfoForSMRequest");
        if (!isStarted)
            return;

        this.countMapSriForSmReq++;

        MAPProvider mapProvider = this.mapMan.getMAPStack().getMAPProvider();
        MAPDialogSms curDialog = sendRoutingInforForSMRequest.getMAPDialog();
        long invokeId = sendRoutingInforForSMRequest.getInvokeId();

        if (!this.testerHost.getConfigurationData().getTestSmsClientConfigurationData().isOneNotificationFor100Dialogs()) {
            String srIforSMReqData = this.createSRIforSMData(sendRoutingInforForSMRequest);
            this.testerHost.sendNotif(SOURCE_NAME, "Rcvd: sriForSMReq", srIforSMReqData, Level.DEBUG);
        }

        IMSI imsi = new IMSIImpl("124356871012345");

        String nnnAddress = "5982123007";
        ISDNAddressString networkNodeNumber = new ISDNAddressStringImpl(AddressNature.international_number,
                NumberingPlan.ISDN, nnnAddress);

        LMSI lmsi = mapProvider.getMAPParameterFactory().createLMSI(new byte[] { 11, 12, 13, 14 });
        MAPExtensionContainer mapExtensionContainer = null;
        AdditionalNumber additionalNumber = null;
        boolean mwdSet = false;
        IpSmGwGuidance ipSmGwGuidance = null;
        LocationInfoWithLMSI locationInfoWithLMSI  = mapProvider.getMAPParameterFactory().createLocationInfoWithLMSI(networkNodeNumber, lmsi, mapExtensionContainer, false, additionalNumber);
        logger.info("LocationInfoWithLMSI for onSendRoutingInfoForSMRequest: NNN="
                +locationInfoWithLMSI.getNetworkNodeNumber().getAddress()+ ", LMSI="+lmsi.getData().toString());


        try {

            curDialog.addSendRoutingInfoForSMResponse(invokeId, imsi, locationInfoWithLMSI, mapExtensionContainer, mwdSet, ipSmGwGuidance);
            curDialog.close(false);

            logger.debug("\nSendRoutingForLCSResponse sent");
            this.countMapSriForSmResp++;

            this.testerHost.sendNotif(SOURCE_NAME, "Sent: SendRoutingForSMResponse",
                    createSRIforSMRespData(curDialog.getLocalDialogId(), imsi, locationInfoWithLMSI), Level.INFO);

        } catch (MAPException e) {
            this.testerHost.sendNotif(SOURCE_NAME, "Exception when invoking addSendRoutingInfoForSMResponse() : " + e.getMessage(), e, Level.ERROR);
        }

    }

    private String createSRIforSMData(SendRoutingInfoForSMRequest sendRoutingInfoForSMRequest) {
        StringBuilder sb = new StringBuilder();
        sb.append("dialogId=");
        sb.append(sendRoutingInfoForSMRequest.getMAPDialog().getLocalDialogId());
        sb.append(",\nsriReq=");
        sb.append(sendRoutingInfoForSMRequest);
        sb.append(",\nRemoteAddress=");
        sb.append(sendRoutingInfoForSMRequest.getMAPDialog().getRemoteAddress());
        sb.append(",\nLocalAddress=");
        sb.append(sendRoutingInfoForSMRequest.getMAPDialog().getLocalAddress());

        return sb.toString();
    }

    private String createSRIforSMRespData(long dialogId, IMSI imsi, LocationInfoWithLMSI locationInfoWithLMSI) {
        StringBuilder sb = new StringBuilder();
        sb.append("dialogId=");
        sb.append(dialogId);
        sb.append(",\n imsi=");
        sb.append(imsi);
        sb.append(",\n locationInfo=");
        sb.append(locationInfoWithLMSI);
        sb.append(",\n");
        return sb.toString();
    }

    //********************//
    //*** PSI methods ***//
    //******************//

    @Override
    public String performProvideSubscriberInfoResponse() {
        if (!isStarted) {
            return "The tester is not started";
        }

        return sendProvideSubscriberInfoResponse();
    }

    public String sendProvideSubscriberInfoResponse() {

        return "sendProvideSubscriberInfoResponse called automatically";
    }

    public void onProvideSubscriberInfoRequest(ProvideSubscriberInfoRequest provideSubscriberInfoRequest) {

        MAPErrorMessage mapErrorMessage = null;
        logger.debug("\nonProvideSubscriberInfoRequest");
        if (!isStarted)
            return;

        this.countMapSriForSmReq++;

        MAPProvider mapProvider = this.mapMan.getMAPStack().getMAPProvider();
        MAPDialogMobility curDialog = provideSubscriberInfoRequest.getMAPDialog();
        long invokeId = provideSubscriberInfoRequest.getInvokeId();

        String psiReqData = this.createPSIReqData(provideSubscriberInfoRequest);
        this.testerHost.sendNotif(SOURCE_NAME, "Rcvd: psiReq", psiReqData, Level.DEBUG);

        SubscriberInfo subscriberInfo = null;
        LocationInformation locationInformation = null;
        MNPInfoRes mnpInfoRes = null;
        int mcc = 748;
        int mnc = 1;
        int lac = 5;
        int ci = 3479;
        CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI = null;
        CellGlobalIdOrServiceAreaIdFixedLength cgiOrSai = null;
        String mscAddress = "5982123007";
        String vlrAddress = "5982123007";
        ISDNAddressString mscNumber = new ISDNAddressStringImpl(AddressNature.international_number,NumberingPlan.ISDN, mscAddress);
        ISDNAddressString vlrNumber = new ISDNAddressStringImpl(AddressNature.international_number,NumberingPlan.ISDN, vlrAddress);
        int aol = 1;
        boolean saiPresent = false;
        GeographicalInformation geographicalInformation = null;
        TypeOfShape geographicalTypeOfShape = TypeOfShape.EllipsoidPointWithUncertaintyCircle;
        Double geographicalLatitude = -23.291032;
        Double geographicalLongitude = 109.977810;
        Double geographicalUncertainty = 50.0;
        GeodeticInformation geodeticInformation = null;
        int screeningAndPresentationIndicators = 3;
        TypeOfShape geodeticTypeOfShape = TypeOfShape.EllipsoidPointWithUncertaintyCircle;
        Double geodeticLatitude = -24.010010;
        Double geodeticLongitude = 110.00987;
        Double geodeticlUncertainty = 100.0;
        int geodeticConfidence = 1;
        LocationInformationEPS locationInformationEPS = null;
        boolean currentLocationRetrieved = true;
        EUtranCgi eUtranCgi = null;
        TAId taId = null;
        MAPExtensionContainer mapExtensionContainer = null;
        byte[] mmeNom = { 10, 32, 71, 4, 0, 18, 7, 59, 24, 77};
        DiameterIdentity mmeName = new DiameterIdentityImpl(mmeNom);
        LSAIdentity lsaIdentity = null;
        LocationNumberMap locationNumberMap = null;
        UserCSGInformation userCSGInformation = null;
        SubscriberState subscriberState = null;
        SubscriberStateChoice subscriberStateChoice = SubscriberStateChoice.assumedIdle;
        NotReachableReason notReachableReason = NotReachableReason.restrictedArea;
        subscriberState = mapProvider.getMAPParameterFactory().createSubscriberState(subscriberStateChoice, notReachableReason);
        LocationInformationGPRS locationInformationGPRS = null;
        PSSubscriberState psSubscriberState = null;
        GPRSMSClass gprsmsClass = null;
        IMEI imei = new IMEIImpl("01171400466105");
        MSClassmark2 msClassmark2 = null;
        RouteingNumber routeingNumber = new RouteingNumberImpl("598123");
        IMSI imsi = new IMSIImpl("748026871012345");
        String msisdnStr = "59899077937";
        ISDNAddressString msisdn = new ISDNAddressStringImpl(AddressNature.international_number,
                NumberingPlan.ISDN, msisdnStr);
        NumberPortabilityStatus numberPortabilityStatus = NumberPortabilityStatus.ownNumberNotPortedOut;
        try {
            MAPParameterFactoryImpl mapFactory = new MAPParameterFactoryImpl();
            CellGlobalIdOrServiceAreaIdFixedLength cellGlobalIdOrServiceAreaIdFixedLength = mapFactory.createCellGlobalIdOrServiceAreaIdFixedLength(mcc, mnc, lac, ci);
            cellGlobalIdOrServiceAreaIdOrLAI = mapFactory.createCellGlobalIdOrServiceAreaIdOrLAI(cellGlobalIdOrServiceAreaIdFixedLength);
            geographicalInformation = new GeographicalInformationImpl(geographicalTypeOfShape, geographicalLatitude, geographicalLongitude, geographicalUncertainty);
            geodeticInformation = new GeodeticInformationImpl(screeningAndPresentationIndicators, geodeticTypeOfShape, geodeticLatitude, geodeticLongitude, geodeticlUncertainty, geodeticConfidence);
            locationInformationEPS = new LocationInformationEPSImpl(eUtranCgi, taId, mapExtensionContainer, geographicalInformation,
                    geodeticInformation, currentLocationRetrieved, aol, mmeName);
            locationInformation = mapProvider.getMAPParameterFactory().createLocationInformation(aol, geographicalInformation,
                    vlrNumber,locationNumberMap, cellGlobalIdOrServiceAreaIdOrLAI, mapExtensionContainer, lsaIdentity, mscNumber, geodeticInformation, currentLocationRetrieved,
                    saiPresent, locationInformationEPS, userCSGInformation);
            mnpInfoRes = mapProvider.getMAPParameterFactory().createMNPInfoRes(routeingNumber, imsi, msisdn, numberPortabilityStatus, mapExtensionContainer);
            subscriberInfo = mapProvider.getMAPParameterFactory().createSubscriberInfo(locationInformation, subscriberState, mapExtensionContainer,
                    locationInformationGPRS, psSubscriberState, imei, msClassmark2, gprsmsClass, mnpInfoRes);
        } catch (MAPException e) {
            this.testerHost.sendNotif(SOURCE_NAME, "Exception when creating subscriber info for PSI response while onProvideSubscriberInfoRequest: " + e.getMessage(), e, Level.ERROR);
        }

        try {

            curDialog.addProvideSubscriberInfoResponse(invokeId, subscriberInfo, mapExtensionContainer);
            curDialog.close(false);

            logger.debug("\nProvideSubscriberInfoResponse sent");
            this.countMapPsiResp++;

            this.testerHost.sendNotif(SOURCE_NAME, "Sent: ProvideSubscriberInfoResponse",
                    createPSIRespData(curDialog.getLocalDialogId(), subscriberInfo, mapExtensionContainer), Level.INFO);

        } catch (MAPException e) {
            this.testerHost.sendNotif(SOURCE_NAME, "Exception when invoking addProvideSubscriberInfoResponse() : " + e.getMessage(), e, Level.ERROR);
        }

    }

    private String createPSIReqData(ProvideSubscriberInfoRequest provideSubscriberInfoRequest) {
        StringBuilder sb = new StringBuilder();
        sb.append("dialogId=");
        sb.append(provideSubscriberInfoRequest.getMAPDialog().getLocalDialogId());
        sb.append(",\nPSI Req=");
        sb.append(provideSubscriberInfoRequest);
        sb.append(",\nPSI Requested Info=");
        sb.append(provideSubscriberInfoRequest.getRequestedInfo());
        sb.append(",\nIMSI=");
        sb.append(provideSubscriberInfoRequest.getImsi());
        sb.append(",\nLMSI=");
        sb.append(provideSubscriberInfoRequest.getLmsi());
        sb.append(",\nRemoteAddress=");
        sb.append(provideSubscriberInfoRequest.getMAPDialog().getRemoteAddress());
        sb.append(",\nLocalAddress=");
        sb.append(provideSubscriberInfoRequest.getMAPDialog().getLocalAddress());
        sb.append(",\nRequested Info=");
        sb.append(provideSubscriberInfoRequest.getRequestedInfo());

        return sb.toString();
    }

    private String createPSIRespData(long dialogId, SubscriberInfo subscriberInfo, MAPExtensionContainer mapExtensionContainer) {

        StringBuilder sb = new StringBuilder();
        sb.append("dialogId=");
        sb.append(dialogId);
        try {
            sb.append(",\nMCC=");
            sb.append(subscriberInfo.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMCC());
            sb.append(",\nMNC=");
            sb.append(subscriberInfo.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMNC());
            sb.append(",\nLAC=");
            sb.append(subscriberInfo.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getLac());
            sb.append(",\nCI=");
            sb.append(subscriberInfo.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getCellIdOrServiceAreaCode());
        } catch (MAPException e) {
            e.printStackTrace();
        }
        sb.append(",\nLocation Information number=");
        sb.append(subscriberInfo.getLocationInformation().getLocationNumber());
        sb.append(",\nMSC number=");
        sb.append(subscriberInfo.getLocationInformation().getMscNumber().getAddress());
        sb.append(",\nVLR number=");
        sb.append(subscriberInfo.getLocationInformation().getVlrNumber().getAddress());
        sb.append(",\nAOL=");
        sb.append(subscriberInfo.getLocationInformation().getAgeOfLocationInformation());
        sb.append(",\nSAI present=");
        sb.append(subscriberInfo.getLocationInformation().getSaiPresent());
        sb.append(",\nGeographicalLatitude=");
        sb.append(subscriberInfo.getLocationInformation().getGeographicalInformation().getLatitude());
        sb.append(",\nGeographical Longitude=");
        sb.append(subscriberInfo.getLocationInformation().getGeographicalInformation().getLongitude());
        sb.append(",\nGeographicalUncertainty=");
        sb.append(subscriberInfo.getLocationInformation().getGeographicalInformation().getUncertainty());
        sb.append(",\nGeographical Type of Shape=");
        sb.append(subscriberInfo.getLocationInformation().getGeographicalInformation().getTypeOfShape());
        sb.append(",\nCurrent Location Retrieved=");
        sb.append(subscriberInfo.getLocationInformation().getCurrentLocationRetrieved());
        sb.append(",\nGeodetic Latitude=");
        sb.append(subscriberInfo.getLocationInformation().getGeodeticInformation().getLatitude());
        sb.append(",\nGeodetic Longitude=");
        sb.append(subscriberInfo.getLocationInformation().getGeodeticInformation().getLongitude());
        sb.append(",\nGeodetic Uncertainty=");
        sb.append(subscriberInfo.getLocationInformation().getGeodeticInformation().getUncertainty());
        sb.append(",\nGeodetic Confidence=");
        sb.append(subscriberInfo.getLocationInformation().getGeodeticInformation().getConfidence());
        sb.append(",\nGeodetic Type of Shape=");
        sb.append(subscriberInfo.getLocationInformation().getGeodeticInformation().getTypeOfShape());
        sb.append(",\nGeodetic Screening and Presentation Indicators=");
        sb.append(subscriberInfo.getLocationInformation().getGeodeticInformation().getScreeningAndPresentationIndicators());
        sb.append(",\nMME name=");
        sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getMmeName());
        sb.append(",\nMNP info result number portability status=");
        sb.append(subscriberInfo.getMNPInfoRes().getNumberPortabilityStatus().getType());
        sb.append(",\nMNP info result MSISDN=");
        sb.append(subscriberInfo.getMNPInfoRes().getMSISDN().getAddress());
        sb.append(",\nMNP info result IMSI=");
        sb.append(subscriberInfo.getMNPInfoRes().getIMSI().getData());
        sb.append(",\nMNP info result MSISDN=");
        sb.append(subscriberInfo.getMNPInfoRes().getMSISDN().getAddress());
        sb.append(",\nMNP info result Routeing Number=");
        sb.append(subscriberInfo.getMNPInfoRes().getRouteingNumber().getRouteingNumber());
        /*sb.append(",\nE-UTRAN CGI=");
        sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getEUtranCellGlobalIdentity());
        sb.append(",\nEPS GeographicalLatitude=");
        sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getGeographicalInformation().getLatitude());
        sb.append(",\nEPS Geographical Longitude=");
        sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getGeographicalInformation().getLongitude());
        sb.append(",\nEPS GeographicalUncertainty=");
        sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getGeographicalInformation().getUncertainty());
        sb.append(",\nEPS Geographical Type of Shape Code=");
        sb.append(subscriberInfo.getLocationInformation().getGeographicalInformation().getTypeOfShape().getCode());
        sb.append(",\nEPS Current Location Retrieved=");
        sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getCurrentLocationRetrieved());
        sb.append(",\nEPS Geodetic Latitude=");
        sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getGeodeticInformation().getLatitude());
        sb.append(",\nEPS Geodetic Longitude=");
        sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getGeodeticInformation().getLongitude());
        sb.append(",\nEPS Geodetic Uncertainty=");
        sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getGeodeticInformation().getUncertainty());
        sb.append(",\nEPS Geodetic Confidence=");
        sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getGeodeticInformation().getConfidence());
        sb.append(",\nEPS Geodetic Type of Shape Code=");
        sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getGeodeticInformation().getTypeOfShape().getCode());
        sb.append(",\nEPS Geodetic Screening and Presentation Indicators=");
        sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getGeodeticInformation().getScreeningAndPresentationIndicators());
        sb.append(",\nEPS AOL=");
        sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getAgeOfLocationInformation());
        sb.append(",\nEPS Current Location Retrieved=");
        sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getCurrentLocationRetrieved());
        sb.append(",\nEPS Traking Identity=");
        sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getTrackingAreaIdentity());*/

        return sb.toString();
    }

    private String createErrorData(long dialogId, int invokeId, MAPErrorMessage mapErrorMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("dialogId=");
        sb.append(dialogId);
        sb.append(",\n invokeId=");
        sb.append(invokeId);
        sb.append(",\n mapErrorMessage=");
        sb.append(mapErrorMessage);
        sb.append(",\n");
        return sb.toString();
    }

    // *** GETTER & SETTERS **//
    //***********************//

    @Override
    public AddressNatureType getAddressNature() {
        return new AddressNatureType(this.testerHost.getConfigurationData().getTestLcsServerConfigurationData().getAddressNature().getIndicator());
    }

    @Override
    public void setAddressNature(AddressNatureType val) {
        this.testerHost.getConfigurationData().getTestLcsServerConfigurationData().setAddressNature(AddressNature.getInstance(val.intValue()));
        this.testerHost.markStore();
    }

    @Override
    public String getNumberingPlan() {
        return this.testerHost.getConfigurationData().getTestLcsServerConfigurationData().getNumberingPlan();
    }

    @Override
    public void setNumberingPlan(String numPlan) {
        this.testerHost.getConfigurationData().getTestLcsServerConfigurationData().setNumberingPlan(numPlan);
        this.testerHost.markStore();
    }

    @Override
    public NumberingPlanMapType getNumberingPlanType() {
        return new NumberingPlanMapType(this.testerHost.getConfigurationData().getTestLcsServerConfigurationData().getNumberingPlanType().getIndicator());
    }

    @Override
    public void setNumberingPlanType(NumberingPlanMapType val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setNumberingPlanType(NumberingPlan.getInstance(val.intValue()));
        this.testerHost.markStore();
    }

    @Override
    public void putAddressNature(String val) {
        AddressNatureType x = AddressNatureType.createInstance(val);
        if (x != null)
            this.setAddressNature(x);
    }

    @Override
    public void putNumberingPlanType(String val) {
        NumberingPlanMapType x = NumberingPlanMapType.createInstance(val);
        if (x != null)
            this.setNumberingPlanType(x);
    }


    /*******************************/

    @Override
    public String getNetworkNodeNumber() {
        return new String(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getNetworkNodeNumber());
    }

    @Override
    public void setNetworkNodeNumber(String val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setNetworkNodeNumber(val);
        this.testerHost.markStore();
    }

    @Override
    public String getImsi() {
        return new String(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getImsi());
    }

    @Override
    public void setImsi(String val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setLmsi(val);
        this.testerHost.markStore();
    }

    @Override
    public String getLmsi() {
        return new String(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getLmsi());
    }

    @Override
    public void setLmsi(String val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setImsi(val);
        this.testerHost.markStore();
    }

    @Override
    public int getMcc() {
        return new Integer(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getMcc());
    }

    @Override
    public void setMcc(int val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setMcc(val);
        this.testerHost.markStore();
    }

    @Override
    public int getMnc() {
        return new Integer(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getMnc());
    }

    @Override
    public void setMnc(int val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setMnc(val);
        this.testerHost.markStore();
    }

    @Override
    public int getLac() {
        return new Integer(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getLac());
    }

    @Override
    public void setLac(int val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setLac(val);
        this.testerHost.markStore();
    }

    @Override
    public int getCi() {
        return new Integer(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getCi());
    }

    @Override
    public void setCi(int val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setCi(val);
        this.testerHost.markStore();
    }

    @Override
    public int getAol() {
        return new Integer(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getAol());
    }

    @Override
    public void setAol(int val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setAol(val);
        this.testerHost.markStore();
    }

    @Override
    public boolean isSaiPresent() {
        return new Boolean(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().isSaiPresent());
    }

    @Override
    public void setSaiPresent(boolean val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setSaiPresent(val);
        this.testerHost.markStore();
    }

    @Override
    public double getGeographicalLatitude() {
        return new Double(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getGeographicalLatitude());
    }

    @Override
    public void setGeographicalLatitude(double val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setGeographicalLatitude(val);
        this.testerHost.markStore();
    }

    @Override
    public double getGeographicalLongitude() {
        return new Double(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getGeographicalLongitude());
    }

    @Override
    public void setGeographicalLongitude(double val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setGeographicalLongitude(val);
        this.testerHost.markStore();
    }

    @Override
    public double getGeographicalUncertainty() {
        return new Double(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getGeographicalUncertainty());
    }

    @Override
    public void setGeographicalUncertainty(double val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setGeographicalLongitude(val);
        this.testerHost.markStore();
    }

    @Override
    public int getScreeningAndPresentationIndicators() {
        return new Integer(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getScreeningAndPresentationIndicators());
    }

    @Override
    public void setScreeningAndPresentationIndicators(int val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setScreeningAndPresentationIndicators(val);
        this.testerHost.markStore();
    }

    @Override
    public double getGeodeticLatitude() {
        return new Double(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getGeodeticLatitude());
    }

    @Override
    public void setGeodeticLatitude(double val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setGeodeticLatitude(val);
        this.testerHost.markStore();
    }

    @Override
    public double getGeodeticLongitude() {
        return new Double(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getGeodeticLongitude());
    }

    @Override
    public void setGeodeticLongitude(double val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setGeodeticLongitude(val);
        this.testerHost.markStore();
    }

    @Override
    public double getGeodeticUncertainty() {
        return new Double(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getGeodeticUncertainty());
    }

    @Override
    public void setGeodeticUncertainty(double val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setGeodeticUncertainty(val);
        this.testerHost.markStore();
    }

    @Override
    public int getGeodeticConfidence() {
        return new Integer(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getGeodeticConfidence());
    }

    @Override
    public void setGeodeticConfidence(int val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setGeodeticConfidence(val);
        this.testerHost.markStore();
    }

    @Override
    public boolean isCurrentLocationRetrieved() {
        return new Boolean(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().isCurrentLocationRetrieved());
    }

    @Override
    public void setCurrentLocationRetrieved(boolean val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setCurrentLocationRetrieved(val);
        this.testerHost.markStore();
    }

    @Override
    public String getImei() {
        return new String(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getImei());
    }

    @Override
    public void setImei(String val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setImei(val);
        this.testerHost.markStore();
    }


    /*******************************/


    @Override
    public SRIReaction getSRIReaction() {
        return this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getSriForSMReaction();
    }

    @Override
    public String getSRIReaction_Value() {
        return this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getSriForSMReaction().toString();
    }

    @Override
    public void setSRIReaction(SRIReaction val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setSriForSMReaction(val);
        this.testerHost.markStore();
    }

    @Override
    public void putSRIReaction(String val) {
        SRIReaction x = SRIReaction.createInstance(val);
        if (x != null)
            this.setSRIReaction(x);
    }

    @Override
    public PSIReaction getPSIReaction() {
        return this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getPsiReaction();
    }

    @Override
    public String getPSIReaction_Value() {
        return this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getPsiReaction().toString();
    }

    @Override
    public void setPSIReaction(PSIReaction val) {
        this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setPsiReaction(val);
        this.testerHost.markStore();
    }

    @Override
    public void putPSIReaction(String val) {
        PSIReaction x = PSIReaction.createInstance(val);
        if (x != null)
            this.setPSIReaction(x);
    }

    @Override
    public String getCurrentRequestDef() {
        return "LastDialog: " + currentRequestDef;
    }


    //** Not needed MAPServiceSms methods **//
    @Override
    public void onForwardShortMessageRequest(ForwardShortMessageRequest forwardShortMessageRequest) {

    }

    @Override
    public void onForwardShortMessageResponse(ForwardShortMessageResponse forwardShortMessageResponse) {

    }

    @Override
    public void onMoForwardShortMessageRequest(MoForwardShortMessageRequest moForwardShortMessageRequest) {

    }

    @Override
    public void onMoForwardShortMessageResponse(MoForwardShortMessageResponse moForwardShortMessageResponse) {

    }

    @Override
    public void onMtForwardShortMessageRequest(MtForwardShortMessageRequest mtForwardShortMessageRequest) {

    }

    @Override
    public void onMtForwardShortMessageResponse(MtForwardShortMessageResponse mtForwardShortMessageResponse) {

    }

    @Override
    public void onSendRoutingInfoForSMResponse(SendRoutingInfoForSMResponse sendRoutingInfoForSMResponse) {

    }

    @Override
    public void onReportSMDeliveryStatusRequest(ReportSMDeliveryStatusRequest reportSMDeliveryStatusRequest) {

    }

    @Override
    public void onReportSMDeliveryStatusResponse(ReportSMDeliveryStatusResponse reportSMDeliveryStatusResponse) {

    }

    @Override
    public void onInformServiceCentreRequest(InformServiceCentreRequest informServiceCentreRequest) {

    }

    @Override
    public void onAlertServiceCentreRequest(AlertServiceCentreRequest alertServiceCentreRequest) {

    }

    @Override
    public void onAlertServiceCentreResponse(AlertServiceCentreResponse alertServiceCentreResponse) {

    }

    @Override
    public void onReadyForSMRequest(ReadyForSMRequest readyForSMRequest) {

    }

    @Override
    public void onReadyForSMResponse(ReadyForSMResponse readyForSMResponse) {

    }

    @Override
    public void onNoteSubscriberPresentRequest(NoteSubscriberPresentRequest noteSubscriberPresentRequest) {

    }

    //** Not needed MAPServiceMobility methods **//
    @Override
    public void onUpdateLocationRequest(UpdateLocationRequest updateLocationRequest) {

    }

    @Override
    public void onUpdateLocationResponse(UpdateLocationResponse updateLocationResponse) {

    }

    @Override
    public void onCancelLocationRequest(CancelLocationRequest cancelLocationRequest) {

    }

    @Override
    public void onCancelLocationResponse(CancelLocationResponse cancelLocationResponse) {

    }

    @Override
    public void onSendIdentificationRequest(SendIdentificationRequest sendIdentificationRequest) {

    }

    @Override
    public void onSendIdentificationResponse(SendIdentificationResponse sendIdentificationResponse) {

    }

    @Override
    public void onUpdateGprsLocationRequest(UpdateGprsLocationRequest updateGprsLocationRequest) {

    }

    @Override
    public void onUpdateGprsLocationResponse(UpdateGprsLocationResponse updateGprsLocationResponse) {

    }

    @Override
    public void onPurgeMSRequest(PurgeMSRequest purgeMSRequest) {

    }

    @Override
    public void onPurgeMSResponse(PurgeMSResponse purgeMSResponse) {

    }

    @Override
    public void onSendAuthenticationInfoRequest(SendAuthenticationInfoRequest sendAuthenticationInfoRequest) {

    }

    @Override
    public void onSendAuthenticationInfoResponse(SendAuthenticationInfoResponse sendAuthenticationInfoResponse) {

    }

    @Override
    public void onAuthenticationFailureReportRequest(AuthenticationFailureReportRequest authenticationFailureReportRequest) {

    }

    @Override
    public void onAuthenticationFailureReportResponse(AuthenticationFailureReportResponse authenticationFailureReportResponse) {

    }

    @Override
    public void onResetRequest(ResetRequest resetRequest) {

    }

    @Override
    public void onForwardCheckSSIndicationRequest(ForwardCheckSSIndicationRequest forwardCheckSSIndicationRequest) {

    }

    @Override
    public void onRestoreDataRequest(RestoreDataRequest restoreDataRequest) {

    }

    @Override
    public void onRestoreDataResponse(RestoreDataResponse restoreDataResponse) {

    }

    @Override
    public void onAnyTimeInterrogationRequest(AnyTimeInterrogationRequest anyTimeInterrogationRequest) {

    }

    @Override
    public void onAnyTimeInterrogationResponse(AnyTimeInterrogationResponse anyTimeInterrogationResponse) {

    }

    @Override
    public void onAnyTimeSubscriptionInterrogationRequest(AnyTimeSubscriptionInterrogationRequest request) {

    }

    @Override
    public void onAnyTimeSubscriptionInterrogationResponse(AnyTimeSubscriptionInterrogationResponse response) {

    }

    @Override
    public void onProvideSubscriberInfoResponse(ProvideSubscriberInfoResponse provideSubscriberInfoResponse) {

    }

    @Override
    public void onInsertSubscriberDataRequest(InsertSubscriberDataRequest insertSubscriberDataRequest) {

    }

    @Override
    public void onInsertSubscriberDataResponse(InsertSubscriberDataResponse insertSubscriberDataResponse) {

    }

    @Override
    public void onDeleteSubscriberDataRequest(DeleteSubscriberDataRequest deleteSubscriberDataRequest) {

    }

    @Override
    public void onDeleteSubscriberDataResponse(DeleteSubscriberDataResponse deleteSubscriberDataResponse) {

    }

    @Override
    public void onCheckImeiRequest(CheckImeiRequest checkImeiRequest) {

    }

    @Override
    public void onCheckImeiResponse(CheckImeiResponse checkImeiResponse) {

    }

    @Override
    public void onActivateTraceModeRequest_Mobility(ActivateTraceModeRequest_Mobility activateTraceModeRequest_mobility) {

    }

    @Override
    public void onActivateTraceModeResponse_Mobility(ActivateTraceModeResponse_Mobility activateTraceModeResponse_mobility) {

    }

}
