--
-- PostgreSQL database dump
--

\restrict HMNIW1myhGmZ1W7ADUpNaju9AYbVzez7wJVJQWKGXNIdiP8z8JzV9cMO7MAVCUP

-- Dumped from database version 18.1
-- Dumped by pg_dump version 18.1

-- Started on 2026-04-25 00:07:46 +03

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 5 (class 2615 OID 16481)
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

-- *not* creating schema, since initdb creates it


ALTER SCHEMA public OWNER TO postgres;

--
-- TOC entry 4545 (class 0 OID 0)
-- Dependencies: 5
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS '';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 220 (class 1259 OID 16848)
-- Name: departments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.departments (
    id bigint NOT NULL,
    description character varying(255),
    name character varying(255),
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone
);


ALTER TABLE public.departments OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16847)
-- Name: departments_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.departments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.departments_id_seq OWNER TO postgres;

--
-- TOC entry 4547 (class 0 OID 0)
-- Dependencies: 219
-- Name: departments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.departments_id_seq OWNED BY public.departments.id;


--
-- TOC entry 222 (class 1259 OID 16858)
-- Name: employees; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.employees (
    id bigint NOT NULL,
    email character varying(255),
    first_name character varying(255),
    hire_date date,
    is_active boolean,
    last_name character varying(255),
    salary numeric(38,2),
    department_id bigint,
    position_id bigint,
    user_id bigint,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone
);


ALTER TABLE public.employees OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16857)
-- Name: employees_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.employees_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.employees_id_seq OWNER TO postgres;

--
-- TOC entry 4548 (class 0 OID 0)
-- Dependencies: 221
-- Name: employees_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.employees_id_seq OWNED BY public.employees.id;


--
-- TOC entry 224 (class 1259 OID 16868)
-- Name: positions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.positions (
    id bigint NOT NULL,
    description character varying(255),
    max_salary numeric(38,2),
    min_salary numeric(38,2),
    name character varying(255),
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone
);


ALTER TABLE public.positions OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 16867)
-- Name: positions_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.positions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.positions_id_seq OWNER TO postgres;

--
-- TOC entry 4549 (class 0 OID 0)
-- Dependencies: 223
-- Name: positions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.positions_id_seq OWNED BY public.positions.id;


--
-- TOC entry 226 (class 1259 OID 16878)
-- Name: roles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.roles (
    id bigint NOT NULL,
    name character varying(255),
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone
);


ALTER TABLE public.roles OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 16877)
-- Name: roles_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.roles_id_seq OWNER TO postgres;

--
-- TOC entry 4550 (class 0 OID 0)
-- Dependencies: 225
-- Name: roles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.roles_id_seq OWNED BY public.roles.id;


--
-- TOC entry 227 (class 1259 OID 16885)
-- Name: user_roles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_roles (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);


ALTER TABLE public.user_roles OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 16893)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    password character varying(255),
    username character varying(255),
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 16892)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO postgres;

--
-- TOC entry 4551 (class 0 OID 0)
-- Dependencies: 228
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- TOC entry 4350 (class 2604 OID 16851)
-- Name: departments id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.departments ALTER COLUMN id SET DEFAULT nextval('public.departments_id_seq'::regclass);


--
-- TOC entry 4351 (class 2604 OID 16861)
-- Name: employees id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees ALTER COLUMN id SET DEFAULT nextval('public.employees_id_seq'::regclass);


--
-- TOC entry 4352 (class 2604 OID 16871)
-- Name: positions id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.positions ALTER COLUMN id SET DEFAULT nextval('public.positions_id_seq'::regclass);


--
-- TOC entry 4353 (class 2604 OID 16881)
-- Name: roles id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles ALTER COLUMN id SET DEFAULT nextval('public.roles_id_seq'::regclass);


--
-- TOC entry 4354 (class 2604 OID 16896)
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- TOC entry 4530 (class 0 OID 16848)
-- Dependencies: 220
-- Data for Name: departments; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.departments (id, description, name, created_at, updated_at) FROM stdin;
1	Platform and product engineering	Engineering	\N	\N
2	Roadmap, discovery and delivery planning	Product	\N	\N
3	UX research and interface design	Design	\N	\N
5	People operations, hiring and development	Human Resources	\N	\N
7	Business development and enterprise sales	Sales	\N	\N
8	Onboarding, support and retention	Customer Success	\N	\N
9	Internal services and business operations	Operations	\N	\N
10	Compliance, contracts and risk management	Legal	\N	\N
4	Analytics, BI and data platform	Dat	\N	2026-04-24 02:41:34.104253
\.


--
-- TOC entry 4532 (class 0 OID 16858)
-- Dependencies: 222
-- Data for Name: employees; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.employees (id, email, first_name, hire_date, is_active, last_name, salary, department_id, position_id, user_id, created_at, updated_at) FROM stdin;
181	ggole.1@gmail.com	12	1212-12-12	t	12	1.00	4	14	\N	2026-04-24 02:31:03.319676	2026-04-24 02:31:03.319769
7	victoria.reyes7@northbridge-tech.com	Victoria	2016-09-30	t	Reyes	3066.00	1	2	\N	\N	\N
14	noah.collins14@northbridge-tech.com	Noah	2017-06-16	t	Collins	5398.00	1	3	\N	\N	\N
21	lily.edwards21@northbridge-tech.com	Lily	2018-03-02	t	Edwards	7252.00	1	4	\N	\N	\N
28	ethan.cruz28@northbridge-tech.com	Ethan	2018-11-16	t	Cruz	2452.00	1	5	\N	\N	\N
35	hannah.parker35@northbridge-tech.com	Hannah	2019-08-02	t	Parker	4710.00	1	6	\N	\N	\N
42	mason.diaz42@northbridge-tech.com	Mason	2020-04-17	t	Diaz	2016.00	1	1	\N	\N	\N
49	nora.turner49@northbridge-tech.com	Nora	2021-01-01	t	Turner	3462.00	1	2	\N	\N	\N
56	lucas.evans56@northbridge-tech.com	Lucas	2021-09-17	t	Evans	5992.00	2	3	\N	\N	\N
63	zoey.phillips63@northbridge-tech.com	Zoey	2022-06-03	t	Phillips	5091.00	2	7	\N	\N	\N
70	james.gomez70@northbridge-tech.com	James	2023-02-17	t	Gomez	5190.00	2	7	\N	\N	\N
77	leah.roberts77@northbridge-tech.com	Leah	2023-11-03	f	Roberts	5289.00	3	7	\N	\N	\N
84	benjamin.carter84@northbridge-tech.com	Benjamin	2024-07-19	t	Carter	3536.00	3	8	\N	\N	\N
91	aria.mitchell91@northbridge-tech.com	Aria	2025-04-04	t	Mitchell	5526.00	4	10	\N	\N	\N
98	henry.campbell98@northbridge-tech.com	Henry	2016-02-10	t	Campbell	6586.00	4	3	\N	\N	\N
105	aubrey.rivera105@northbridge-tech.com	Aubrey	2016-10-26	t	Rivera	3110.00	5	11	\N	\N	\N
112	alexander.hall112@northbridge-tech.com	Alexander	2017-07-12	t	Hall	4848.00	5	12	\N	\N	\N
119	claire.baker119@northbridge-tech.com	Claire	2018-03-28	t	Baker	3218.00	5	11	\N	\N	\N
147	madison.flores147@northbridge-tech.com	Madison	2021-01-27	t	Flores	4831.00	7	15	\N	\N	\N
154	william.hill154@northbridge-tech.com	William	2021-10-13	f	Hill	6140.00	7	16	\N	\N	\N
161	penelope.nguyen161@northbridge-tech.com	Penelope	2022-06-29	t	Nguyen	2973.00	8	18	\N	\N	\N
168	david.torres168@northbridge-tech.com	David	2023-03-15	t	Torres	4816.00	8	17	\N	\N	\N
175	layla.scott175@northbridge-tech.com	Layla	2023-11-29	t	Scott	9625.00	9	22	\N	\N	\N
1	amelia.moore1@northbridge-tech.com	Amelia	2016-02-21	t	Moore	10160.00	1	21	1	\N	\N
2	joseph.wright2@northbridge-tech.com	Joseph	2016-03-29	t	Wright	9030.00	1	22	2	\N	\N
3	zoey.phillips3@northbridge-tech.com	Zoey	2016-05-05	t	Phillips	9436.00	1	4	3	\N	\N
4	thomas.rodriguez4@northbridge-tech.com	Thomas	2016-06-11	t	Rodriguez	2536.00	1	5	4	\N	\N
5	ava.sanchez5@northbridge-tech.com	Ava	2016-07-18	t	Sanchez	5730.00	1	6	5	\N	\N
6	daniel.nelson6@northbridge-tech.com	Daniel	2016-08-24	t	Nelson	2688.00	1	1	6	\N	\N
8	gabriel.taylor8@northbridge-tech.com	Gabriel	2016-11-06	t	Taylor	6256.00	1	3	7	\N	\N
9	riley.king9@northbridge-tech.com	Riley	2016-12-13	t	King	8708.00	1	4	8	\N	\N
10	james.gomez10@northbridge-tech.com	James	2017-01-19	t	Gomez	4090.00	1	5	9	\N	\N
11	ella.davis11@northbridge-tech.com	Ella	2017-02-25	f	Davis	4846.00	1	6	10	\N	\N
12	anthony.harris12@northbridge-tech.com	Anthony	2017-04-03	t	Harris	2376.00	1	1	11	\N	\N
15	harper.thomas15@northbridge-tech.com	Harper	2017-07-23	t	Thomas	7980.00	1	4	13	\N	\N
16	samuel.allen16@northbridge-tech.com	Samuel	2017-08-29	t	Allen	3544.00	1	5	14	\N	\N
17	leah.roberts17@northbridge-tech.com	Leah	2017-10-05	t	Roberts	7362.00	1	6	15	\N	\N
18	adrian.miller18@northbridge-tech.com	Adrian	2017-11-11	t	Miller	2064.00	1	1	16	\N	\N
19	sophia.white19@northbridge-tech.com	Sophia	2017-12-18	t	White	4122.00	1	2	17	\N	\N
20	michael.green20@northbridge-tech.com	Michael	2018-01-24	t	Green	7840.00	1	3	18	\N	\N
22	dylan.anderson22@northbridge-tech.com	Dylan	2018-04-08	f	Anderson	2998.00	1	5	19	\N	\N
23	stella.young23@northbridge-tech.com	Stella	2018-05-15	t	Young	6478.00	1	6	20	\N	\N
24	benjamin.carter24@northbridge-tech.com	Benjamin	2018-06-21	t	Carter	2952.00	1	1	21	\N	\N
25	scarlett.garcia25@northbridge-tech.com	Scarlett	2018-07-28	t	Garcia	3550.00	1	2	22	\N	\N
26	nathan.thompson26@northbridge-tech.com	Nathan	2018-09-03	t	Thompson	6982.00	1	3	23	\N	\N
27	madison.flores27@northbridge-tech.com	Madison	2018-10-10	t	Flores	9324.00	1	4	24	\N	\N
29	evelyn.wilson29@northbridge-tech.com	Evelyn	2018-12-23	t	Wilson	5594.00	1	6	25	\N	\N
30	jack.walker30@northbridge-tech.com	Jack	2019-01-29	t	Walker	2640.00	1	1	26	\N	\N
31	aria.mitchell31@northbridge-tech.com	Aria	2019-03-07	t	Mitchell	5178.00	1	2	27	\N	\N
32	jonathan.jones32@northbridge-tech.com	Jonathan	2019-04-13	t	Jones	6124.00	1	3	28	\N	\N
33	mia.perez33@northbridge-tech.com	Mia	2019-05-20	f	Perez	8596.00	1	4	29	\N	\N
34	william.hill34@northbridge-tech.com	William	2019-06-26	t	Hill	4006.00	1	5	30	\N	\N
36	isaac.gonzalez36@northbridge-tech.com	Isaac	2019-09-08	t	Gonzalez	2328.00	1	1	31	\N	\N
37	olivia.robinson37@northbridge-tech.com	Olivia	2019-10-15	t	Robinson	4606.00	1	2	32	\N	\N
38	henry.campbell38@northbridge-tech.com	Henry	2019-11-21	t	Campbell	5266.00	1	3	33	\N	\N
39	grace.brown39@northbridge-tech.com	Grace	2019-12-28	t	Brown	7868.00	1	4	34	\N	\N
40	christopher.lee40@northbridge-tech.com	Christopher	2020-02-03	t	Lee	3460.00	1	5	35	\N	\N
41	penelope.nguyen41@northbridge-tech.com	Penelope	2020-03-11	t	Nguyen	7226.00	1	6	36	\N	\N
43	abigail.lopez43@northbridge-tech.com	Abigail	2020-05-24	t	Lopez	4034.00	1	2	37	\N	\N
44	matthew.lewis44@northbridge-tech.com	Matthew	2020-06-30	f	Lewis	7708.00	1	3	38	\N	\N
45	aubrey.rivera45@northbridge-tech.com	Aubrey	2020-08-06	t	Rivera	7140.00	1	4	39	\N	\N
46	aaron.williams46@northbridge-tech.com	Aaron	2020-09-12	t	Williams	2914.00	1	5	40	\N	\N
47	charlotte.martin47@northbridge-tech.com	Charlotte	2020-10-19	t	Martin	6342.00	1	6	41	\N	\N
48	david.torres48@northbridge-tech.com	David	2020-11-25	t	Torres	2904.00	1	1	42	\N	\N
50	caleb.hernandez50@northbridge-tech.com	Caleb	2021-02-07	t	Hernandez	6850.00	1	3	43	\N	\N
51	emma.ramirez51@northbridge-tech.com	Emma	2021-03-16	t	Ramirez	9212.00	1	4	44	\N	\N
52	alexander.hall52@northbridge-tech.com	Alexander	2021-04-22	t	Hall	2368.00	1	5	45	\N	\N
53	chloe.johnson53@northbridge-tech.com	Chloe	2021-05-29	t	Johnson	5458.00	1	6	46	\N	\N
54	ryan.jackson54@northbridge-tech.com	Ryan	2021-07-05	t	Jackson	2592.00	1	1	47	\N	\N
55	layla.scott55@northbridge-tech.com	Layla	2021-08-11	f	Scott	5090.00	1	2	48	\N	\N
57	emily.martinez57@northbridge-tech.com	Emily	2021-10-24	t	Martinez	5949.00	2	7	49	\N	\N
58	andrew.clark58@northbridge-tech.com	Andrew	2021-11-30	t	Clark	6906.00	2	7	50	\N	\N
59	claire.baker59@northbridge-tech.com	Claire	2022-01-06	t	Baker	5563.00	2	3	51	\N	\N
60	liam.smith60@northbridge-tech.com	Liam	2022-02-12	t	Smith	5520.00	2	7	52	\N	\N
61	amelia.moore61@northbridge-tech.com	Amelia	2022-03-21	t	Moore	6477.00	2	7	53	\N	\N
62	joseph.wright62@northbridge-tech.com	Joseph	2022-04-27	t	Wright	8434.00	2	3	54	\N	\N
64	thomas.rodriguez64@northbridge-tech.com	Thomas	2022-07-10	t	Rodriguez	6048.00	2	7	55	\N	\N
65	ava.sanchez65@northbridge-tech.com	Ava	2022-08-16	t	Sanchez	8005.00	2	3	56	\N	\N
66	daniel.nelson66@northbridge-tech.com	Daniel	2022-09-22	f	Nelson	4662.00	2	7	57	\N	\N
67	victoria.reyes67@northbridge-tech.com	Victoria	2022-10-29	t	Reyes	5619.00	2	7	58	\N	\N
68	gabriel.taylor68@northbridge-tech.com	Gabriel	2022-12-05	t	Taylor	7576.00	2	3	59	\N	\N
69	riley.king69@northbridge-tech.com	Riley	2023-01-11	t	King	4233.00	2	7	60	\N	\N
71	ella.davis71@northbridge-tech.com	Ella	2023-03-26	t	Davis	7147.00	2	3	61	\N	\N
72	anthony.harris72@northbridge-tech.com	Anthony	2023-05-02	t	Harris	7104.00	2	7	62	\N	\N
74	noah.collins74@northbridge-tech.com	Noah	2023-07-15	t	Collins	6718.00	2	3	64	\N	\N
75	harper.thomas75@northbridge-tech.com	Harper	2023-08-21	t	Thomas	4550.00	3	8	65	\N	\N
76	samuel.allen76@northbridge-tech.com	Samuel	2023-09-27	t	Allen	2704.00	3	8	66	\N	\N
78	adrian.miller78@northbridge-tech.com	Adrian	2023-12-10	t	Miller	4212.00	3	8	67	\N	\N
79	sophia.white79@northbridge-tech.com	Sophia	2024-01-16	t	White	4966.00	3	8	68	\N	\N
80	michael.green80@northbridge-tech.com	Michael	2024-02-22	t	Green	4860.00	3	7	69	\N	\N
81	lily.edwards81@northbridge-tech.com	Lily	2024-03-30	t	Edwards	3874.00	3	8	70	\N	\N
82	dylan.anderson82@northbridge-tech.com	Dylan	2024-05-06	t	Anderson	4628.00	3	8	71	\N	\N
83	stella.young83@northbridge-tech.com	Stella	2024-06-12	t	Young	4431.00	3	7	72	\N	\N
85	scarlett.garcia85@northbridge-tech.com	Scarlett	2024-08-25	t	Garcia	4290.00	3	8	73	\N	\N
86	nathan.thompson86@northbridge-tech.com	Nathan	2024-10-01	t	Thompson	7302.00	3	7	74	\N	\N
87	madison.flores87@northbridge-tech.com	Madison	2024-11-07	t	Flores	3306.00	4	9	75	\N	\N
88	ethan.cruz88@northbridge-tech.com	Ethan	2024-12-14	f	Cruz	5968.00	4	10	76	\N	\N
89	evelyn.wilson89@northbridge-tech.com	Evelyn	2025-01-20	t	Wilson	7873.00	4	3	77	\N	\N
90	jack.walker90@northbridge-tech.com	Jack	2025-02-26	t	Walker	3020.00	4	9	78	\N	\N
92	jonathan.jones92@northbridge-tech.com	Jonathan	2025-05-11	t	Jones	7444.00	4	3	79	\N	\N
93	mia.perez93@northbridge-tech.com	Mia	2025-06-17	t	Perez	4934.00	4	9	80	\N	\N
94	william.hill94@northbridge-tech.com	William	2025-07-24	t	Hill	5084.00	4	10	81	\N	\N
95	hannah.parker95@northbridge-tech.com	Hannah	2025-08-30	t	Parker	7015.00	4	3	82	\N	\N
96	isaac.gonzalez96@northbridge-tech.com	Isaac	2025-10-06	t	Gonzalez	4648.00	4	9	83	\N	\N
97	olivia.robinson97@northbridge-tech.com	Olivia	2025-11-12	t	Robinson	4642.00	4	10	84	\N	\N
99	grace.brown99@northbridge-tech.com	Grace	2016-03-18	f	Brown	4362.00	4	9	85	\N	\N
100	christopher.lee100@northbridge-tech.com	Christopher	2016-04-24	t	Lee	4200.00	4	10	86	\N	\N
101	penelope.nguyen101@northbridge-tech.com	Penelope	2016-05-31	t	Nguyen	6157.00	4	3	87	\N	\N
102	mason.diaz102@northbridge-tech.com	Mason	2016-07-07	t	Diaz	4076.00	4	9	88	\N	\N
103	abigail.lopez103@northbridge-tech.com	Abigail	2016-08-13	t	Lopez	7158.00	4	10	89	\N	\N
104	matthew.lewis104@northbridge-tech.com	Matthew	2016-09-19	t	Lewis	5728.00	4	3	90	\N	\N
106	aaron.williams106@northbridge-tech.com	Aaron	2016-12-02	t	Williams	5524.00	5	12	91	\N	\N
107	charlotte.martin107@northbridge-tech.com	Charlotte	2017-01-08	t	Martin	2354.00	5	11	92	\N	\N
108	david.torres108@northbridge-tech.com	David	2017-02-14	t	Torres	2876.00	5	11	93	\N	\N
109	nora.turner109@northbridge-tech.com	Nora	2017-03-23	t	Turner	5186.00	5	12	94	\N	\N
110	caleb.hernandez110@northbridge-tech.com	Caleb	2017-04-29	f	Hernandez	3920.00	5	11	95	\N	\N
111	emma.ramirez111@northbridge-tech.com	Emma	2017-06-05	t	Ramirez	2642.00	5	11	96	\N	\N
113	chloe.johnson113@northbridge-tech.com	Chloe	2017-08-18	t	Johnson	3686.00	5	11	97	\N	\N
114	ryan.jackson114@northbridge-tech.com	Ryan	2017-09-24	t	Jackson	2408.00	5	11	98	\N	\N
115	layla.scott115@northbridge-tech.com	Layla	2017-10-31	t	Scott	4510.00	5	12	99	\N	\N
116	lucas.evans116@northbridge-tech.com	Lucas	2017-12-07	t	Evans	3452.00	5	11	100	\N	\N
117	emily.martinez117@northbridge-tech.com	Emily	2018-01-13	t	Martinez	3974.00	5	11	101	\N	\N
118	andrew.clark118@northbridge-tech.com	Andrew	2018-02-19	t	Clark	4172.00	5	12	102	\N	\N
120	liam.smith120@northbridge-tech.com	Liam	2018-05-04	t	Smith	3740.00	5	11	103	\N	\N
121	amelia.moore121@northbridge-tech.com	Amelia	2018-06-10	f	Moore	3834.00	5	12	104	\N	\N
122	joseph.wright122@northbridge-tech.com	Joseph	2018-07-17	t	Wright	2984.00	5	11	105	\N	\N
141	lily.edwards141@northbridge-tech.com	Lily	2020-06-19	t	Edwards	5793.00	7	15	121	\N	\N
142	dylan.anderson142@northbridge-tech.com	Dylan	2020-07-26	t	Anderson	4220.00	7	16	122	\N	\N
143	stella.young143@northbridge-tech.com	Stella	2020-09-01	f	Young	4239.00	7	15	123	\N	\N
144	benjamin.carter144@northbridge-tech.com	Benjamin	2020-10-08	t	Carter	5312.00	7	15	124	\N	\N
145	scarlett.garcia145@northbridge-tech.com	Scarlett	2020-11-14	t	Garcia	3700.00	7	16	125	\N	\N
146	nathan.thompson146@northbridge-tech.com	Nathan	2020-12-21	t	Thompson	3758.00	7	15	126	\N	\N
148	ethan.cruz148@northbridge-tech.com	Ethan	2021-03-05	t	Cruz	7180.00	7	16	127	\N	\N
149	evelyn.wilson149@northbridge-tech.com	Evelyn	2021-04-11	t	Wilson	3277.00	7	15	128	\N	\N
150	jack.walker150@northbridge-tech.com	Jack	2021-05-18	t	Walker	4350.00	7	15	129	\N	\N
151	aria.mitchell151@northbridge-tech.com	Aria	2021-06-24	t	Mitchell	6660.00	7	16	130	\N	\N
152	jonathan.jones152@northbridge-tech.com	Jonathan	2021-07-31	t	Jones	2796.00	7	15	131	\N	\N
153	mia.perez153@northbridge-tech.com	Mia	2021-09-06	t	Perez	3869.00	7	15	132	\N	\N
155	hannah.parker155@northbridge-tech.com	Hannah	2021-11-19	t	Parker	6015.00	7	15	133	\N	\N
156	isaac.gonzalez156@northbridge-tech.com	Isaac	2021-12-26	t	Gonzalez	3388.00	7	15	134	\N	\N
157	olivia.robinson157@northbridge-tech.com	Olivia	2022-02-01	t	Robinson	5620.00	7	16	135	\N	\N
158	henry.campbell158@northbridge-tech.com	Henry	2022-03-10	t	Campbell	5534.00	7	15	136	\N	\N
159	grace.brown159@northbridge-tech.com	Grace	2022-04-16	t	Brown	2907.00	7	15	137	\N	\N
160	christopher.lee160@northbridge-tech.com	Christopher	2022-05-23	t	Lee	5100.00	7	16	138	\N	\N
162	mason.diaz162@northbridge-tech.com	Mason	2022-08-05	t	Diaz	5544.00	8	17	139	\N	\N
163	abigail.lopez163@northbridge-tech.com	Abigail	2022-09-11	t	Lopez	2259.00	8	18	140	\N	\N
164	matthew.lewis164@northbridge-tech.com	Matthew	2022-10-18	t	Lewis	2752.00	8	18	141	\N	\N
165	aubrey.rivera165@northbridge-tech.com	Aubrey	2022-11-24	f	Rivera	5180.00	8	17	142	\N	\N
166	aaron.williams166@northbridge-tech.com	Aaron	2022-12-31	t	Williams	2038.00	8	18	143	\N	\N
167	charlotte.martin167@northbridge-tech.com	Charlotte	2023-02-06	t	Martin	2531.00	8	18	144	\N	\N
169	nora.turner169@northbridge-tech.com	Nora	2023-04-21	t	Turner	1817.00	8	18	145	\N	\N
170	caleb.hernandez170@northbridge-tech.com	Caleb	2023-05-28	t	Hernandez	2310.00	8	18	146	\N	\N
171	emma.ramirez171@northbridge-tech.com	Emma	2023-07-04	t	Ramirez	4452.00	8	17	147	\N	\N
172	alexander.hall172@northbridge-tech.com	Alexander	2023-08-10	t	Hall	3296.00	8	18	148	\N	\N
173	chloe.johnson173@northbridge-tech.com	Chloe	2023-09-16	t	Johnson	3659.00	9	19	149	\N	\N
174	ryan.jackson174@northbridge-tech.com	Ryan	2023-10-23	t	Jackson	4442.00	9	19	150	\N	\N
176	lucas.evans176@northbridge-tech.com	Lucas	2024-01-05	f	Evans	3308.00	9	19	151	\N	\N
177	emily.martinez177@northbridge-tech.com	Emily	2024-02-11	t	Martinez	4091.00	9	19	152	\N	\N
178	andrew.clark178@northbridge-tech.com	Andrew	2024-03-19	t	Clark	6794.00	10	20	153	\N	\N
179	claire.baker179@northbridge-tech.com	Claire	2024-04-25	t	Baker	5657.00	10	19	154	\N	\N
180	liam.smith180@northbridge-tech.com	Liam	2024-06-01	t	Smith	5240.00	10	20	155	\N	\N
\.


--
-- TOC entry 4534 (class 0 OID 16868)
-- Dependencies: 224
-- Data for Name: positions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.positions (id, description, max_salary, min_salary, name, created_at, updated_at) FROM stdin;
1	Entry-level software engineer	3000.00	1800.00	Junior Software Engineer	\N	\N
2	Mid-level backend or frontend engineer	5200.00	3000.00	Software Engineer	\N	\N
3	Senior engineer with system ownership	8500.00	5200.00	Senior Software Engineer	\N	\N
4	Technical lead across product area	9800.00	7000.00	Lead Software Engineer	\N	\N
5	Manual and automation quality engineer	4300.00	2200.00	QA Engineer	\N	\N
6	CI/CD, cloud and reliability engineering	7600.00	4200.00	DevOps Engineer	\N	\N
7	Product planning and prioritization	7500.00	4200.00	Product Manager	\N	\N
8	Product and interface design specialist	5200.00	2600.00	UI/UX Designer	\N	\N
9	Business analytics and KPI reporting	5000.00	2800.00	Data Analyst	\N	\N
10	Data pipelines and warehouse engineering	7600.00	4200.00	Data Engineer	\N	\N
11	Recruitment and people operations	4100.00	2300.00	HR Specialist	\N	\N
12	Leads talent and people management	6200.00	3600.00	HR Manager	\N	\N
13	Budgeting and financial analysis	5600.00	3000.00	Financial Analyst	\N	\N
14	Accounting operations and reporting	4300.00	2400.00	Accountant	\N	\N
15	Owns outbound sales pipeline	6200.00	2500.00	Sales Executive	\N	\N
16	Manages enterprise accounts and renewals	7500.00	3500.00	Account Executive	\N	\N
17	Account health and adoption	5600.00	2800.00	Customer Success Manager	\N	\N
18	Customer incident and ticket support	3500.00	1800.00	Support Specialist	\N	\N
19	Coordinates internal operations	5900.00	3200.00	Operations Manager	\N	\N
20	Corporate legal support and compliance	8200.00	4500.00	Legal Counsel	\N	\N
21	Department leadership and strategy	13000.00	9000.00	Head of Engineering	\N	\N
22	Operations leadership and governance	10500.00	7000.00	Head of Operations	\N	\N
\.


--
-- TOC entry 4536 (class 0 OID 16878)
-- Dependencies: 226
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.roles (id, name, created_at, updated_at) FROM stdin;
1	ROLE_ADMIN	\N	\N
2	ROLE_MANAGER	\N	\N
3	ROLE_HR	\N	\N
4	ROLE_FINANCE	\N	\N
5	ROLE_USER	\N	\N
\.


--
-- TOC entry 4537 (class 0 OID 16885)
-- Dependencies: 227
-- Data for Name: user_roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_roles (user_id, role_id) FROM stdin;
1	5
2	5
3	5
4	5
5	5
6	5
7	5
8	5
9	5
10	5
11	5
13	5
14	5
15	5
16	5
17	5
18	5
19	5
20	5
21	5
22	5
23	5
24	5
25	5
26	5
27	5
28	5
29	5
30	5
31	5
32	5
33	5
34	5
35	5
36	5
37	5
38	5
39	5
40	5
41	5
42	5
43	5
44	5
45	5
46	5
47	5
48	5
49	5
50	5
51	5
52	5
53	5
54	5
55	5
56	5
57	5
58	5
59	5
60	5
61	5
62	5
64	5
65	5
66	5
67	5
68	5
69	5
70	5
71	5
72	5
73	5
74	5
75	5
76	5
77	5
78	5
79	5
80	5
81	5
82	5
83	5
84	5
85	5
86	5
87	5
88	5
89	5
90	5
91	5
92	5
93	5
94	5
95	5
96	5
97	5
98	5
99	5
100	5
101	5
102	5
103	5
104	5
105	5
121	5
122	5
123	5
124	5
125	5
126	5
127	5
128	5
129	5
130	5
131	5
132	5
133	5
134	5
135	5
136	5
137	5
138	5
139	5
140	5
141	5
142	5
143	5
144	5
145	5
146	5
147	5
148	5
149	5
150	5
151	5
152	5
153	5
154	5
155	5
91	3
92	3
93	3
94	3
95	3
96	3
97	3
98	3
99	3
100	3
101	3
102	3
103	3
104	3
105	3
1	2
2	2
3	2
8	2
13	2
24	2
29	2
34	2
39	2
44	2
49	2
50	2
52	2
53	2
55	2
57	2
58	2
60	2
62	2
69	2
72	2
74	2
91	2
94	2
99	2
102	2
104	2
122	2
125	2
127	2
130	2
135	2
138	2
139	2
142	2
147	2
149	2
150	2
151	2
152	2
154	2
1	1
2	1
\.


--
-- TOC entry 4539 (class 0 OID 16893)
-- Dependencies: 229
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, password, username, created_at, updated_at) FROM stdin;
1	Welcome123!	amelia.moore1	\N	\N
2	Welcome123!	joseph.wright2	\N	\N
3	Welcome123!	zoey.phillips3	\N	\N
4	Welcome123!	thomas.rodriguez4	\N	\N
5	Welcome123!	ava.sanchez5	\N	\N
6	Welcome123!	daniel.nelson6	\N	\N
7	Welcome123!	gabriel.taylor8	\N	\N
8	Welcome123!	riley.king9	\N	\N
9	Welcome123!	james.gomez10	\N	\N
10	Welcome123!	ella.davis11	\N	\N
11	Welcome123!	anthony.harris12	\N	\N
13	Welcome123!	harper.thomas15	\N	\N
14	Welcome123!	samuel.allen16	\N	\N
15	Welcome123!	leah.roberts17	\N	\N
16	Welcome123!	adrian.miller18	\N	\N
17	Welcome123!	sophia.white19	\N	\N
18	Welcome123!	michael.green20	\N	\N
19	Welcome123!	dylan.anderson22	\N	\N
20	Welcome123!	stella.young23	\N	\N
21	Welcome123!	benjamin.carter24	\N	\N
22	Welcome123!	scarlett.garcia25	\N	\N
23	Welcome123!	nathan.thompson26	\N	\N
24	Welcome123!	madison.flores27	\N	\N
25	Welcome123!	evelyn.wilson29	\N	\N
26	Welcome123!	jack.walker30	\N	\N
27	Welcome123!	aria.mitchell31	\N	\N
28	Welcome123!	jonathan.jones32	\N	\N
29	Welcome123!	mia.perez33	\N	\N
30	Welcome123!	william.hill34	\N	\N
31	Welcome123!	isaac.gonzalez36	\N	\N
32	Welcome123!	olivia.robinson37	\N	\N
33	Welcome123!	henry.campbell38	\N	\N
34	Welcome123!	grace.brown39	\N	\N
35	Welcome123!	christopher.lee40	\N	\N
36	Welcome123!	penelope.nguyen41	\N	\N
37	Welcome123!	abigail.lopez43	\N	\N
38	Welcome123!	matthew.lewis44	\N	\N
39	Welcome123!	aubrey.rivera45	\N	\N
40	Welcome123!	aaron.williams46	\N	\N
41	Welcome123!	charlotte.martin47	\N	\N
42	Welcome123!	david.torres48	\N	\N
43	Welcome123!	caleb.hernandez50	\N	\N
44	Welcome123!	emma.ramirez51	\N	\N
45	Welcome123!	alexander.hall52	\N	\N
46	Welcome123!	chloe.johnson53	\N	\N
47	Welcome123!	ryan.jackson54	\N	\N
48	Welcome123!	layla.scott55	\N	\N
49	Welcome123!	emily.martinez57	\N	\N
50	Welcome123!	andrew.clark58	\N	\N
51	Welcome123!	claire.baker59	\N	\N
52	Welcome123!	liam.smith60	\N	\N
53	Welcome123!	amelia.moore61	\N	\N
54	Welcome123!	joseph.wright62	\N	\N
55	Welcome123!	thomas.rodriguez64	\N	\N
56	Welcome123!	ava.sanchez65	\N	\N
57	Welcome123!	daniel.nelson66	\N	\N
58	Welcome123!	victoria.reyes67	\N	\N
59	Welcome123!	gabriel.taylor68	\N	\N
60	Welcome123!	riley.king69	\N	\N
61	Welcome123!	ella.davis71	\N	\N
62	Welcome123!	anthony.harris72	\N	\N
64	Welcome123!	noah.collins74	\N	\N
65	Welcome123!	harper.thomas75	\N	\N
66	Welcome123!	samuel.allen76	\N	\N
67	Welcome123!	adrian.miller78	\N	\N
68	Welcome123!	sophia.white79	\N	\N
69	Welcome123!	michael.green80	\N	\N
70	Welcome123!	lily.edwards81	\N	\N
71	Welcome123!	dylan.anderson82	\N	\N
72	Welcome123!	stella.young83	\N	\N
73	Welcome123!	scarlett.garcia85	\N	\N
74	Welcome123!	nathan.thompson86	\N	\N
75	Welcome123!	madison.flores87	\N	\N
76	Welcome123!	ethan.cruz88	\N	\N
77	Welcome123!	evelyn.wilson89	\N	\N
78	Welcome123!	jack.walker90	\N	\N
79	Welcome123!	jonathan.jones92	\N	\N
80	Welcome123!	mia.perez93	\N	\N
81	Welcome123!	william.hill94	\N	\N
82	Welcome123!	hannah.parker95	\N	\N
83	Welcome123!	isaac.gonzalez96	\N	\N
84	Welcome123!	olivia.robinson97	\N	\N
85	Welcome123!	grace.brown99	\N	\N
86	Welcome123!	christopher.lee100	\N	\N
87	Welcome123!	penelope.nguyen101	\N	\N
88	Welcome123!	mason.diaz102	\N	\N
89	Welcome123!	abigail.lopez103	\N	\N
90	Welcome123!	matthew.lewis104	\N	\N
91	Welcome123!	aaron.williams106	\N	\N
92	Welcome123!	charlotte.martin107	\N	\N
93	Welcome123!	david.torres108	\N	\N
94	Welcome123!	nora.turner109	\N	\N
95	Welcome123!	caleb.hernandez110	\N	\N
96	Welcome123!	emma.ramirez111	\N	\N
97	Welcome123!	chloe.johnson113	\N	\N
98	Welcome123!	ryan.jackson114	\N	\N
99	Welcome123!	layla.scott115	\N	\N
100	Welcome123!	lucas.evans116	\N	\N
101	Welcome123!	emily.martinez117	\N	\N
102	Welcome123!	andrew.clark118	\N	\N
103	Welcome123!	liam.smith120	\N	\N
104	Welcome123!	amelia.moore121	\N	\N
105	Welcome123!	joseph.wright122	\N	\N
121	Welcome123!	lily.edwards141	\N	\N
122	Welcome123!	dylan.anderson142	\N	\N
123	Welcome123!	stella.young143	\N	\N
124	Welcome123!	benjamin.carter144	\N	\N
125	Welcome123!	scarlett.garcia145	\N	\N
126	Welcome123!	nathan.thompson146	\N	\N
127	Welcome123!	ethan.cruz148	\N	\N
128	Welcome123!	evelyn.wilson149	\N	\N
129	Welcome123!	jack.walker150	\N	\N
130	Welcome123!	aria.mitchell151	\N	\N
131	Welcome123!	jonathan.jones152	\N	\N
132	Welcome123!	mia.perez153	\N	\N
133	Welcome123!	hannah.parker155	\N	\N
134	Welcome123!	isaac.gonzalez156	\N	\N
135	Welcome123!	olivia.robinson157	\N	\N
136	Welcome123!	henry.campbell158	\N	\N
137	Welcome123!	grace.brown159	\N	\N
138	Welcome123!	christopher.lee160	\N	\N
139	Welcome123!	mason.diaz162	\N	\N
140	Welcome123!	abigail.lopez163	\N	\N
141	Welcome123!	matthew.lewis164	\N	\N
142	Welcome123!	aubrey.rivera165	\N	\N
143	Welcome123!	aaron.williams166	\N	\N
144	Welcome123!	charlotte.martin167	\N	\N
145	Welcome123!	nora.turner169	\N	\N
146	Welcome123!	caleb.hernandez170	\N	\N
147	Welcome123!	emma.ramirez171	\N	\N
148	Welcome123!	alexander.hall172	\N	\N
149	Welcome123!	chloe.johnson173	\N	\N
150	Welcome123!	ryan.jackson174	\N	\N
151	Welcome123!	lucas.evans176	\N	\N
152	Welcome123!	emily.martinez177	\N	\N
153	Welcome123!	andrew.clark178	\N	\N
154	Welcome123!	claire.baker179	\N	\N
155	Welcome123!	liam.smith180	\N	\N
\.


--
-- TOC entry 4552 (class 0 OID 0)
-- Dependencies: 219
-- Name: departments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.departments_id_seq', 10, true);


--
-- TOC entry 4553 (class 0 OID 0)
-- Dependencies: 221
-- Name: employees_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.employees_id_seq', 182, true);


--
-- TOC entry 4554 (class 0 OID 0)
-- Dependencies: 223
-- Name: positions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.positions_id_seq', 22, true);


--
-- TOC entry 4555 (class 0 OID 0)
-- Dependencies: 225
-- Name: roles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.roles_id_seq', 5, true);


--
-- TOC entry 4556 (class 0 OID 0)
-- Dependencies: 228
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 155, true);


--
-- TOC entry 4356 (class 2606 OID 16856)
-- Name: departments departments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.departments
    ADD CONSTRAINT departments_pkey PRIMARY KEY (id);


--
-- TOC entry 4360 (class 2606 OID 16866)
-- Name: employees employees_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_pkey PRIMARY KEY (id);


--
-- TOC entry 4364 (class 2606 OID 16876)
-- Name: positions positions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.positions
    ADD CONSTRAINT positions_pkey PRIMARY KEY (id);


--
-- TOC entry 4368 (class 2606 OID 16884)
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- TOC entry 4366 (class 2606 OID 16907)
-- Name: positions uk_3vhyopdpf9huqh1t67ho6nayj; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.positions
    ADD CONSTRAINT uk_3vhyopdpf9huqh1t67ho6nayj UNIQUE (name);


--
-- TOC entry 4362 (class 2606 OID 16905)
-- Name: employees uk_j2dmgsma6pont6kf7nic9elpd; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT uk_j2dmgsma6pont6kf7nic9elpd UNIQUE (user_id);


--
-- TOC entry 4358 (class 2606 OID 16903)
-- Name: departments uk_j6cwks7xecs5jov19ro8ge3qk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.departments
    ADD CONSTRAINT uk_j6cwks7xecs5jov19ro8ge3qk UNIQUE (name);


--
-- TOC entry 4370 (class 2606 OID 16909)
-- Name: roles uk_ofx66keruapi6vyqpv6f2or37; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT uk_ofx66keruapi6vyqpv6f2or37 UNIQUE (name);


--
-- TOC entry 4374 (class 2606 OID 16911)
-- Name: users uk_r43af9ap4edm43mmtq01oddj6; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_r43af9ap4edm43mmtq01oddj6 UNIQUE (username);


--
-- TOC entry 4372 (class 2606 OID 16891)
-- Name: user_roles user_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id);


--
-- TOC entry 4376 (class 2606 OID 16901)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 4377 (class 2606 OID 16922)
-- Name: employees fk69x3vjuy1t5p18a5llb8h2fjx; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT fk69x3vjuy1t5p18a5llb8h2fjx FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- TOC entry 4378 (class 2606 OID 16912)
-- Name: employees fkgy4qe3dnqrm3ktd76sxp7n4c2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT fkgy4qe3dnqrm3ktd76sxp7n4c2 FOREIGN KEY (department_id) REFERENCES public.departments(id);


--
-- TOC entry 4380 (class 2606 OID 16927)
-- Name: user_roles fkh8ciramu9cc9q3qcqiv4ue8a6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fkh8ciramu9cc9q3qcqiv4ue8a6 FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- TOC entry 4381 (class 2606 OID 16932)
-- Name: user_roles fkhfh9dx7w3ubf1co1vdev94g3f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- TOC entry 4379 (class 2606 OID 16917)
-- Name: employees fkngcpgx7fx5kednw3m7u0u8of3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT fkngcpgx7fx5kednw3m7u0u8of3 FOREIGN KEY (position_id) REFERENCES public.positions(id);


--
-- TOC entry 4546 (class 0 OID 0)
-- Dependencies: 5
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2026-04-25 00:07:46 +03

--
-- PostgreSQL database dump complete
--

\unrestrict HMNIW1myhGmZ1W7ADUpNaju9AYbVzez7wJVJQWKGXNIdiP8z8JzV9cMO7MAVCUP

