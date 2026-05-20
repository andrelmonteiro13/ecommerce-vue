import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import CustomerDetails from './customer-details.vue';
import CustomerService from './customer.service';

type CustomerDetailsComponentType = InstanceType<typeof CustomerDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const customerSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Customer Management Detail Component', () => {
    let customerServiceStub: SinonStubbedInstance<CustomerService>;
    let mountOptions: MountingOptions<CustomerDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      customerServiceStub = sinon.createStubInstance<CustomerService>(CustomerService);

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
          customerService: () => customerServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        customerServiceStub.find.resolves(customerSample);
        route = {
          params: {
            customerId: `${123}`,
          },
        };
        const wrapper = shallowMount(CustomerDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.customer).toMatchObject(customerSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        customerServiceStub.find.resolves(customerSample);
        const wrapper = shallowMount(CustomerDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
