import type { AttributeInputMap } from '$lib/components/CreateServiceRequest/ServiceDefinitionAttributes/shared';
import type { AxiosInstance } from 'axios';
import axios from 'axios';
import { z } from 'zod';

const JurisdicationIdSchema = z.string();
const HasJurisdictionIdSchema = z.object({
	jurisdiction_id: JurisdicationIdSchema
});
export type HasJurisdictionId = z.infer<typeof HasJurisdictionIdSchema>;
export type JurisdictionId = z.infer<typeof JurisdicationIdSchema>;

const RealtimeServiceTypeSchema = z.literal('realtime');
const OtherServiceTypeSchema = z.literal('other'); // todo remove once second type is found
const ServiceTypeSchema = z.union([RealtimeServiceTypeSchema, OtherServiceTypeSchema]); // todo what are the other types besides realtime?

const ServiceCodeSchema = z.string();
const HasServiceCodeSchema = z.object({
	service_code: ServiceCodeSchema
});

export type HasServiceCode = z.infer<typeof HasServiceCodeSchema>;
export type ServiceCode = z.infer<typeof ServiceCodeSchema>;

export const ServiceSchema = z
	.object({
		service_name: z.string(),
		description: z.string(),
		metadata: z.boolean(),
		type: ServiceTypeSchema
		// keywords: z.array(z.string()),
		// group: z.string()
	})
	.merge(HasServiceCodeSchema);

export type Service = z.infer<typeof ServiceSchema>;

const StringType = z.literal('string');
const NumberType = z.literal('number');
const DatetimeType = z.literal('datetime');
const TextType = z.literal('text');
const SingleValueListType = z.literal('singlevaluelist');
const MultiValueListType = z.literal('multivaluelist');

const DatatypeUnionSchema = z.union([
	StringType,
	NumberType,
	DatetimeType,
	TextType,
	SingleValueListType,
	MultiValueListType
]);

// const ServiceDefinitionAttributeCode
export const BaseServiceDefinitionAttributeSchema = z.object({
	/**
	 * true: denotes that user input is needed
	 * false: means the attribute is only used to present information to the user within the description field
	 */
	variable: z.boolean(),
	code: z.string(),
	datatype: DatatypeUnionSchema,
	required: z.boolean(),
	/**
	 * A description of the datatype which helps the user provide their input (the placeholder text essentially)
	 */
	datatype_description: z.string().nullish(),
	order: z.number(),
	/**
	 * The actual question
	 */
	description: z.string()
});

export type BaseServiceDefinitionAttribute = z.infer<typeof BaseServiceDefinitionAttributeSchema>;

export const StringServiceDefinitionAttributeSchema = BaseServiceDefinitionAttributeSchema.extend({
	datatype: StringType
});

export type StringServiceDefinitionAttribute = z.infer<
	typeof StringServiceDefinitionAttributeSchema
>;

export const DateTimeServiceDefinitionAttributeSchema = BaseServiceDefinitionAttributeSchema.extend(
	{
		datatype: DatetimeType
	}
);

export type DateTimeServiceDefinitionAttribute = z.infer<
	typeof DateTimeServiceDefinitionAttributeSchema
>;

export const NumberServiceDefinitionAttributeSchema = BaseServiceDefinitionAttributeSchema.extend({
	datatype: NumberType
});

export type NumberServiceDefinitionAttribute = z.infer<
	typeof NumberServiceDefinitionAttributeSchema
>;

export const TextServiceDefinitionAttributeSchema = BaseServiceDefinitionAttributeSchema.extend({
	datatype: TextType
});

export type TextServiceDefinitionAttribute = z.infer<typeof TextServiceDefinitionAttributeSchema>;

const AttributeValueSchema = z.object({
	/**
	 * The unique identifier associated with an option for singlevaluelist or multivaluelist. This is analogous to the value attribute in an html option tag.
	 */
	key: z.string(),
	/**
	 * The human readable title of an option for singlevaluelist or multivaluelist. This is analogous to the innerhtml text node of an html option tag.
	 */
	name: z.string()
});

export type AttributeValue = z.infer<typeof AttributeValueSchema>;

export const ListBasedServiceDefinitionAttributeSchema =
	BaseServiceDefinitionAttributeSchema.extend({
		datatype: z.union([SingleValueListType, MultiValueListType]),
		values: z.array(AttributeValueSchema)
	});

export const MultiSelectServiceDefinitionAttributeSchema =
	ListBasedServiceDefinitionAttributeSchema.extend({
		datatype: MultiValueListType
	});

export const SingleValueListServiceDefinitionAttributeSchema =
	ListBasedServiceDefinitionAttributeSchema.extend({
		datatype: SingleValueListType
	});

export type MultiSelectServiceDefinitionAttribute = z.infer<
	typeof MultiSelectServiceDefinitionAttributeSchema
>;

export type SingleValueListServiceDefinitionAttribute = z.infer<
	typeof SingleValueListServiceDefinitionAttributeSchema
>;

export const ServiceDefinitionAttributeSchema = z.union([
	StringServiceDefinitionAttributeSchema,
	DateTimeServiceDefinitionAttributeSchema,
	TextServiceDefinitionAttributeSchema,
	NumberServiceDefinitionAttributeSchema,
	MultiSelectServiceDefinitionAttributeSchema,
	SingleValueListServiceDefinitionAttributeSchema
]);

export type ServiceDefinitionAttribute = z.infer<typeof ServiceDefinitionAttributeSchema>;

const ServiceDefinitionSchema = HasServiceCodeSchema.extend({
	attributes: z.array(ServiceDefinitionAttributeSchema)
});

export type ServiceDefinition = z.infer<typeof ServiceDefinitionSchema>;

const HasServiceRequestIdSchema = z.object({
	service_request_id: z.number()
});

export type HasServiceRequestId = z.infer<typeof HasServiceRequestIdSchema>;
export type ServiceRequestId = HasServiceRequestId['service_request_id'];

export const CreateServiceRequestResponseSchema = z
	.object({
		service_notice: z.string().nullish(),
		account_id: z.number().nullish()
	})
	.merge(HasServiceRequestIdSchema);

export type CreateServiceRequestResponse = z.infer<typeof CreateServiceRequestResponseSchema>;

export const GetServiceListResponseSchema = z.array(ServiceSchema);
export type GetServiceListResponse = z.infer<typeof GetServiceListResponseSchema>;

// user response values from  ServiceDefinitionAttributeSchema.
// attribute[code1]=value1
// todo consider adding AttributeValue[name] so that ui can reflect the values later on
export type AttributeResponse = { code: ServiceDefinitionAttribute['code']; value: string };
// todo will likely need the recaptcha value here

export const EmailSchema = z.string().email();

export const ContactInformationSchema = z.object({
	first_name: z.string().optional(),
	last_name: z.string().optional(),
	phone: z.string().optional(), // todo add validation => https://www.npmjs.com/package/libphonenumber-js
	email: EmailSchema.optional()
});

export type ContactInformation = z.infer<typeof ContactInformationSchema>;

export type CreateServiceRequestParams = ContactInformation & {
	lat: string;
	lng: string;
	address_string: string;
	attributeMap: AttributeInputMap;
	description?: string;
	media_url?: string;
	service: Service;
};

export const OpenServiceRequestStatusSchema = z.literal('Open');
export const ClosedServiceRequestStatusSchema = z.literal('Closed');
export const ServiceRequestStatusSchema = z.union([
	OpenServiceRequestStatusSchema,
	ClosedServiceRequestStatusSchema
]);
export type ServiceRequestStatus = z.infer<typeof ServiceRequestStatusSchema>;

export const ServiceRequestSchema = z
	.object({
		status: ServiceRequestStatusSchema,
		status_notes: z.string().nullish(),
		service_name: z.string(),
		description: z.string().nullish(), // this seems like it should be required as a bareminimum
		// detail: z.array(z.string()),
		agency_responsible: z.string().nullish(), // SeeClickFix guarantees this as known at time of creation
		service_notice: z.string().nullish(),
		requested_datetime: z.string(),
		updated_datetime: z.string(),
		expected_datetime: z.string().nullish(),
		address: z.string(),
		address_id: z.number().nullish(),
		zipcode: z.string(),
		lat: z.string(),
		long: z.string(),
		media_url: z.string().nullish()
	})
	.merge(HasServiceRequestIdSchema)
	.merge(HasServiceCodeSchema)
	.merge(ContactInformationSchema);

export type ServiceRequest = z.infer<typeof ServiceRequestSchema>;

export const GetServiceRequestsResponseSchema = z.array(ServiceRequestSchema);
export type GetServiceRequestsResponse = z.infer<typeof GetServiceRequestsResponseSchema>;

export type GetServiceRequestsParams = {
	serviceCode?: ServiceCode;
	startDate?: string;
	endDate?: string;
	status?: ServiceRequestStatus[];
	pageNumber?: number;
};

const JurisdictionConfigSchema = z
	.object({
		jurisdiction_name: z.string()
	})
	.merge(HasJurisdictionIdSchema);

export type JurisdictionConfig = z.infer<typeof JurisdictionConfigSchema>;

const PaginationSchema = z.object({
	size: z.number(), // the number of records per page
	offset: z.number(), // if pageSize = 10 and pageNumber = 5 then offset = 50,
	pageNumber: z.number(), // the current page number (first page starts at 0)
	totalPages: z.number(), // the total number of pages
	totalSize: z.number() // the total number of records
});

export type Pagination = z.infer<typeof PaginationSchema>;

export const EMPTY_PAGINATION = {
	size: 0,
	offset: 0,
	pageNumber: 0,
	totalPages: 0,
	totalSize: 0
};

export type HasPagination = {
	pagination: Pagination;
};

export type HasMetadata<T> = {
	metadata: T;
};

export type ServiceRequestsResponse = {
	serviceRequests: GetServiceRequestsResponse;
} & HasMetadata<HasPagination>;

// https://wiki.open311.org/GeoReport_v2/
export interface Open311Service {
	getServiceList(): Promise<GetServiceListResponse>;
	getServiceDefinition(params: HasServiceCode): Promise<ServiceDefinition>;
	createServiceRequest(params: CreateServiceRequestParams): Promise<CreateServiceRequestResponse>;
	getServiceRequests(params: GetServiceRequestsParams): Promise<ServiceRequestsResponse>;
	getServiceRequest(params: HasServiceRequestId): Promise<ServiceRequest>;
}

export const ReverseGeocodeResponseSchema = z.object({
	display_name: z.string()
});

export type ReverseGeocodeResponse = z.infer<typeof ReverseGeocodeResponseSchema>;

export interface Libre311Service extends Open311Service {
	getJurisdictionConfig(): JurisdictionConfig;
	reverseGeocode(coords: L.PointTuple): Promise<ReverseGeocodeResponse>;
	uploadImage(file: File): Promise<unknown>;
}

const Libre311ServicePropsSchema = z.object({
	baseURL: z.string()
});

export type Libre311ServiceProps = z.infer<typeof Libre311ServicePropsSchema>;

const ROUTES = {
	getJurisdictionConfig: '/config',
	getServiceList: (params: HasJurisdictionId) =>
		`/services?jurisdiction_id=${params.jurisdiction_id}`,
	getServiceDefinition: (params: HasJurisdictionId & HasServiceCode) =>
		`/services/${params.service_code}?jurisdiction_id=${params.jurisdiction_id}`,
	getServiceRequests: (qParams: URLSearchParams) => `/requests?${qParams.toString()}`
};

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function getJurisdictionConfig(): Promise<JurisdictionConfig> {
	// todo don't use axios (will need to create axiosInstance or pass in the baseUrl)
	const res = await axios.get<unknown>(ROUTES.getJurisdictionConfig);
	return JurisdictionConfigSchema.parse(res.data);
}

export class Libre311ServiceImpl implements Libre311Service {
	private axiosInstance: AxiosInstance;
	private jurisdictionId: JurisdictionId;
	private jurisdictionConfig: JurisdictionConfig;

	private constructor(props: Libre311ServiceProps & { jurisdictionConfig: JurisdictionConfig }) {
		Libre311ServicePropsSchema.parse(props);
		this.axiosInstance = axios.create({ baseURL: props.baseURL });
		this.jurisdictionConfig = props.jurisdictionConfig;
		this.jurisdictionId = props.jurisdictionConfig.jurisdiction_id;
	}

	public static async create(props: Libre311ServiceProps) {
		// todo uncomment when /config endpoint exists code the jurisdiction_id
		// const jurisdictionConfig = await getJurisdictionConfig();
		const jurisdictionConfig = {
			jurisdiction_id: 'town.gov',
			jurisdiction_name: 'Fayetteville, AR'
		};
		return new Libre311ServiceImpl({ ...props, jurisdictionConfig });
	}

	getJurisdictionConfig(): JurisdictionConfig {
		return this.jurisdictionConfig;
	}

	async reverseGeocode(coords: L.PointTuple): Promise<ReverseGeocodeResponse> {
		const url = `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${coords[0]}&lon=${coords[1]}`;
		const res = await axios.get<unknown>(url);
		return ReverseGeocodeResponseSchema.parse(res.data);
	}

	async getServiceList(): Promise<GetServiceListResponse> {
		const res = await this.axiosInstance.get<unknown>(
			ROUTES.getServiceList({ jurisdiction_id: this.jurisdictionId })
		);

		return GetServiceListResponseSchema.parse(res.data);
	}

	async getServiceDefinition(params: HasServiceCode): Promise<ServiceDefinition> {
		const res = await this.axiosInstance.get<unknown>(
			ROUTES.getServiceDefinition({ ...params, ...{ jurisdiction_id: this.jurisdictionId } })
		);
		return ServiceDefinitionSchema.parse(res.data);
	}

	async createServiceRequest(
		params: CreateServiceRequestParams
	): Promise<CreateServiceRequestResponse> {
		// todo transform CreateServiceRequestParams into backend value
		console.log(params);
		throw Error('Not Implemented');
	}

	async getServiceRequests(params: GetServiceRequestsParams): Promise<ServiceRequestsResponse> {
		const queryParams = new URLSearchParams();
		queryParams.append('jurisdiction_id', this.jurisdictionId);
		queryParams.append('page_size', '10');
		queryParams.append('page', `${params.pageNumber ?? 0}`);

		const res = await this.axiosInstance.get<unknown>(ROUTES.getServiceRequests(queryParams));
		const serviceRequests = GetServiceRequestsResponseSchema.parse(res.data);
		const pagination: Pagination = {
			offset: res.headers['page-offset'],
			pageNumber: res.headers['page-pagenumber'],
			size: res.headers['page-totalsize'],
			totalPages: res.headers['page-totalpages'],
			totalSize: res.headers['page-totalsize']
		};

		return {
			serviceRequests,
			metadata: {
				pagination
			}
		};
	}

	async getServiceRequest(params: HasServiceRequestId): Promise<ServiceRequest> {
		console.log(params);
		throw Error('Not Implemented');
	}

	async uploadImage(file: File): Promise<unknown> {
		throw Error('Not Implemented');
	}
}

export async function libre311Factory(props: Libre311ServiceProps): Promise<Libre311Service> {
	return Libre311ServiceImpl.create(props);
}
