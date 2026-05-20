import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import ShipmentDetails from './shipment-details.vue';
import ShipmentService from './shipment.service';

type ShipmentDetailsComponentType = InstanceType<typeof ShipmentDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const shipmentSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Shipment Management Detail Component', () => {
    let shipmentServiceStub: SinonStubbedInstance<ShipmentService>;
    let mountOptions: MountingOptions<ShipmentDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      shipmentServiceStub = sinon.createStubInstance<ShipmentService>(ShipmentService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        toast: {
          show: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          shipmentService: () => shipmentServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        shipmentServiceStub.find.resolves(shipmentSample);
        route = {
          params: {
            shipmentId: `${123}`,
          },
        };
        const wrapper = shallowMount(ShipmentDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.shipment).toMatchObject(shipmentSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        shipmentServiceStub.find.resolves(shipmentSample);
        const wrapper = shallowMount(ShipmentDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
