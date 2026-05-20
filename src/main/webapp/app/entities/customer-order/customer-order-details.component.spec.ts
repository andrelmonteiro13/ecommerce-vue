import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import CustomerOrderDetails from './customer-order-details.vue';
import CustomerOrderService from './customer-order.service';

type CustomerOrderDetailsComponentType = InstanceType<typeof CustomerOrderDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const customerOrderSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('CustomerOrder Management Detail Component', () => {
    let customerOrderServiceStub: SinonStubbedInstance<CustomerOrderService>;
    let mountOptions: MountingOptions<CustomerOrderDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      customerOrderServiceStub = sinon.createStubInstance<CustomerOrderService>(CustomerOrderService);

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
          customerOrderService: () => customerOrderServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        customerOrderServiceStub.find.resolves(customerOrderSample);
        route = {
          params: {
            customerOrderId: `${123}`,
          },
        };
        const wrapper = shallowMount(CustomerOrderDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.customerOrder).toMatchObject(customerOrderSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        customerOrderServiceStub.find.resolves(customerOrderSample);
        const wrapper = shallowMount(CustomerOrderDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
